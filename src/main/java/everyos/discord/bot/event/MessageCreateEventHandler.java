package everyos.discord.bot.event;

import java.util.HashMap;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.PermissionOverwrite;
import discord4j.core.object.entity.channel.GuildChannel;
import discord4j.core.object.entity.channel.GuildMessageChannel;
import discord4j.rest.util.Permission;
import discord4j.rest.util.PermissionSet;
import discord4j.rest.util.Snowflake;
import everyos.discord.bot.BotInstance;
import everyos.discord.bot.adapter.ChannelUserAdapter;
import everyos.discord.bot.adapter.GuildAdapter;
import everyos.discord.bot.adapter.MemberAdapter;
import everyos.discord.bot.command.CommandData;
import everyos.discord.bot.command.IGroupCommand;
import everyos.discord.bot.database.DBArray;
import everyos.discord.bot.database.DBObject;
import everyos.discord.bot.localization.Localization;
import everyos.discord.bot.localization.LocalizationProvider;
import everyos.discord.bot.usercase.DefaultUserCase;
import everyos.discord.bot.usercase.IgnoreUserCase;
import everyos.discord.bot.util.ErrorUtil;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class MessageCreateEventHandler {
	private BotInstance bot = null;
	private HashMap<String, IGroupCommand> usercase;
	public MessageCreateEventHandler(BotInstance bot) {
		this.bot = bot;
		
		usercase = new HashMap<String, IGroupCommand>();
		usercase.put("default", new DefaultUserCase(bot));
		usercase.put("ignore", new IgnoreUserCase(bot)); //NOTE: User cases are per-channel, while ignore is guild-wide. Must handle
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
                    .flatMap(channel->MemberAdapter.of(GuildAdapter.of(bot, channel), message.getAuthor().get().getId().asLong()).getDocument())
                    .flatMap(document->{
                    	DBObject object = document.getObject();
                    	
                        long uid = message.getAuthor().get().getId().asLong();
                        long time = 1000*15;
                        long curtime = System.currentTimeMillis();
                        if (curtime-object.getOrDefaultLong("lastmsg", -time)>=time) {
                        	object.set("feth", object.getOrDefaultLong("feth", 0)+1);
                        	object.set("xp", object.getOrDefaultLong("xp", 0)+1); //TODO: Level awards
                            object.set("lastmsg", curtime);
                            return document.save();
                        }
                        return Mono.empty();
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
            				return GuildAdapter.of(bot, guild).getDocument().map(doc->doc.getObject()).flatMap(obj->{
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
            		CommandData data = new CommandData(bot);
            		return ChannelUserAdapter.of(bot, message.getChannelId().asLong(), message.getAuthor().get()).getDocument()
            			.map(document->document.getObject())
            			.flatMap(obj->{
            				String cmode = obj.getOrDefaultString("case", "default");
			                if (channel instanceof GuildMessageChannel) {
			                	long uid = message.getAuthor().get().getId().asLong();
			                	return MemberAdapter.of(GuildAdapter.of(bot, (GuildMessageChannel) channel), uid).getDocument().map(doc->{
			                		return obj.getOrDefaultBoolean("ignored", false)?"ignore":cmode;
			                	});
			                }
			                return Mono.just(cmode);
			            }).flatMap(cmode->{
			                data.locale = new LocalizationProvider(Localization.en_US);
			                data.event = event;
			                data.usercase = usercase.getOrDefault(cmode, usercase.get("default"));
			                data.prefixes = new String[]{"---", "*", "<@"+data.bot.clientID+">", "<@!"+data.bot.clientID+">"};
			
			                Mono<?> mono = Mono.empty();
			                if (channel instanceof GuildChannel) {
			                	mono = GuildAdapter.of(data.bot, ((GuildChannel) channel).getGuildId().asLong()).getDocument()
			                		.doOnNext(doc->{
			                			DBArray prefixes = doc.getObject().getOrCreateArray("prefixes", ()->DBArray.from(data.prefixes));
			                			
			                			data.prefixes = prefixes.toArray(new String[prefixes.getLength()]);
			                		});
			                }
			                
			                Mono<?> mono2 = data.usercase.execute(message, data, message.getContent());
			                if (mono2 == null) return Mono.empty();
			                
			                return mono
			                	.then(mono2)
			                	.cast(Object.class)
			                    .onErrorResume(e->{return ErrorUtil.handleError(e, channel, data.locale);});
            			});
            	});
            })
            .onErrorResume(e->{e.printStackTrace(); return Mono.empty();});
	}
}
