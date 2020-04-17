package everyos.discord.bot.event;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.PermissionOverwrite;
import discord4j.core.object.entity.channel.GuildMessageChannel;
import discord4j.rest.util.Permission;
import discord4j.rest.util.PermissionSet;
import discord4j.rest.util.Snowflake;
import everyos.discord.bot.ShardInstance;
import everyos.discord.bot.adapter.ChannelUserAdapter;
import everyos.discord.bot.adapter.GuildAdapter;
import everyos.discord.bot.adapter.MemberAdapter;
import everyos.discord.bot.command.CommandData;
import everyos.discord.bot.command.IGroupCommand;
import everyos.discord.bot.localization.Localization;
import everyos.discord.bot.localization.LocalizationProvider;
import everyos.discord.bot.usercase.DefaultUserCase;
import everyos.discord.bot.usercase.IgnoreUserCase;
import everyos.discord.bot.util.ErrorUtil;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class MessageCreateEventHandler {
	private ShardInstance shard = null;
	private HashMap<String, IGroupCommand> usercase;
	public MessageCreateEventHandler(ShardInstance shard) {
		this.shard = shard;
		
		usercase = new HashMap<String, IGroupCommand>();
		usercase.put("default", new DefaultUserCase(shard));
		usercase.put("ignore", new IgnoreUserCase(shard)); //NOTE: User cases are per-channel, while ignore is guild-wide. Must handle
	}
	
	public Flux<?> handle(MessageCreateEvent event) {
		return Flux.just(event.getMessage())
            .filter(message->message.getAuthor().isPresent()&&!message.getAuthor().get().isBot())
            .doOnNext(message->{
                message
                    .getChannel()
                    .doOnError(e->e.printStackTrace())
                    .filter(channel->channel instanceof GuildMessageChannel)
                    .cast(GuildMessageChannel.class)
                    .map(channel->GuildAdapter.of(shard, channel))
                    .doOnNext(adapter->{
                        long uid = message.getAuthor().get().getId().asLong();
                        MemberAdapter.of(adapter, uid).getDocument().getObject((object, document)->{
                            long time = 1000*15;
                            long curtime = System.currentTimeMillis();
                            if (curtime-object.getOrDefaultLong("lastmsg", -time)>=time) {
                            	object.set("feth", object.getOrDefaultLong("feth", 0)+1);
                            	object.set("xp", object.getOrDefaultLong("xp", 0)+1); //TODO: Level awards
                                object.set("lastmsg", curtime);
                                document.save();
                            }
                        });
                    })
                    .onErrorResume(e->{e.printStackTrace(); return Mono.empty();})
                    .subscribe();
            })
            .map(message->{
                return message; //TODO: Filter
            })
            .flatMap(message->{
            	return message.getChannel().flatMap(channel->{
            		if (channel instanceof GuildMessageChannel) {
            			return message.getGuild().flatMap(guild->{
            				return GuildAdapter.of(shard, guild).getData(obj->{
            					if (obj.has("muteid")) {
            						Snowflake muteID = Snowflake.of(obj.getOrDefaultString("muteid", null));
            						return message.getAuthorAsMember().flatMap(member->{
            							if (!member.getRoleIds().contains(muteID)) return Mono.just(message);
        								return message.delete().then(
    										((GuildMessageChannel) channel).addRoleOverwrite(
        										muteID, PermissionOverwrite.forRole(
        											muteID,
        											PermissionSet.none(),
        											PermissionSet.of(Permission.SEND_MESSAGES, Permission.USE_EXTERNAL_EMOJIS)
        								))).then(Mono.empty());
            						});
            					}
            					
            					return Mono.just(message);
            				});
            			});
            		}
            		
            		return Mono.just(message);
            	});
            })
            .flatMap(message->{
            	return message.getChannel().flatMap(channel->{
	                AtomicReference<String> cmode = new AtomicReference<String>();
	                ChannelUserAdapter.of(shard, message.getChannelId().asLong(), message.getAuthor().get()).getData(obj->{
	                    cmode.set(obj.getOrDefaultString("case", "default"));
	                });
	                if (channel instanceof GuildMessageChannel) {
	                	long uid = message.getAuthor().get().getId().asLong();
	                	MemberAdapter.of(GuildAdapter.of(shard, (GuildMessageChannel) channel), uid).getDocument().getObject(obj->{
	                		if (obj.getOrDefaultBoolean("ignored", false)) cmode.set("ignore");
	                	});
	                }
	                
	                LocalizationProvider provider = new LocalizationProvider(Localization.en_US);
	                CommandData data = new CommandData(provider, shard);
	                
	                data.event = event;
	                data.usercase = usercase.getOrDefault(cmode.get(), usercase.get("default"));
	
	                Mono<?> mono = data.usercase.execute(message, data, message.getContent());
	                if (mono == null) return Mono.empty();
	                return mono
	                	.cast(Object.class)
	                    .onErrorResume(e->{return ErrorUtil.handleError(e, channel, data.locale); });
            	});
            })
            .onErrorResume(e->{e.printStackTrace(); return Mono.empty();});
	}
}
