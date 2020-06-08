package everyos.discord.luwu.event;

import java.util.HashMap;

import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.PermissionOverwrite;
import discord4j.core.object.entity.channel.GuildChannel;
import discord4j.core.object.entity.channel.GuildMessageChannel;
import discord4j.rest.util.Permission;
import discord4j.rest.util.PermissionSet;
import everyos.discord.luwu.BotInstance;
import everyos.discord.luwu.adapter.ChannelUserAdapter;
import everyos.discord.luwu.adapter.GuildAdapter;
import everyos.discord.luwu.adapter.MemberAdapter;
import everyos.discord.luwu.command.CommandData;
import everyos.discord.luwu.command.IGroupCommand;
import everyos.discord.luwu.database.DBArray;
import everyos.discord.luwu.database.DBObject;
import everyos.discord.luwu.localization.Localization;
import everyos.discord.luwu.localization.LocalizationProvider;
import everyos.discord.luwu.localization.LocalizedString;
import everyos.discord.luwu.usercase.DefaultUserCase;
import everyos.discord.luwu.usercase.IgnoreUserCase;
import everyos.discord.luwu.util.ErrorUtil;
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
            						Snowflake muteID = Snowflake.of(obj.getOrDefaultLong("muteid", -1L));
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
			                		return doc.getObject().getOrDefaultBoolean("ignored", false)?"ignore":cmode;
			                	});
			                }
			                return Mono.just(cmode);
			            }).flatMap(cmode->{
			            	String dprefix1 = "<@"+data.bot.clientID+">";
			            	String dprefix2 = "<@!"+data.bot.clientID+">";
			            	
			                data.locale = new LocalizationProvider(Localization.en_US);
			                data.event = event;
			                data.usercase = usercase.getOrDefault(cmode, usercase.get("default"));
			                data.prefixes = new String[]{"luwu", "*", dprefix1, dprefix2};
			
			                Mono<?> mono = Mono.empty();
			                if (channel instanceof GuildChannel) {
			                	mono = GuildAdapter.of(data.bot, ((GuildChannel) channel).getGuildId().asLong()).getDocument()
			                		.doOnNext(doc->{
			                			DBArray prefixes = doc.getObject().getOrCreateArray("prefixes", ()->DBArray.from(data.prefixes));
			                			if (!prefixes.contains(dprefix1)) prefixes.add(dprefix1);
			                			if (!prefixes.contains(dprefix2)) prefixes.add(dprefix2);
			                			
			                			data.prefixes = prefixes.toArray(new String[prefixes.getLength()]);
			                		});
			                }
			                
			                String content = message.getContent();
			                if (!cmode.equals("ignore")&&(content.equals(dprefix1)||content.equals(dprefix2)))
			                	return channel.createMessage(data.localize(LocalizedString.PrefixPing));
			                
			                Mono<?> mono2 = data.usercase.execute(message, data, content);
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
