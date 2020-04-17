package everyos.discord.bot.command.moderation;

import java.util.HashMap;

import discord4j.core.object.entity.Message;
import discord4j.rest.util.Permission;
import everyos.discord.bot.adapter.ChannelAdapter;
import everyos.discord.bot.adapter.ChatLinkAdapter;
import everyos.discord.bot.command.CommandData;
import everyos.discord.bot.command.ICommand;
import everyos.discord.bot.command.IGroupCommand;
import everyos.discord.bot.command.InvalidSubcommand;
import everyos.discord.bot.localization.Localization;
import everyos.discord.bot.localization.LocalizedString;
import everyos.discord.bot.parser.ArgumentParser;
import everyos.discord.bot.standards.ChatLinkDocumentCreateStandard;
import everyos.discord.bot.util.ErrorUtil.LocalizedException;
import everyos.discord.bot.util.PermissionUtil;
import everyos.storage.database.DBArray;
import reactor.core.publisher.Mono;

public class ChatLinkManagerCommand implements IGroupCommand {
	public HashMap<Localization, HashMap<String, ICommand>> lcommands;
    public ChatLinkManagerCommand() {
        lcommands = new HashMap<Localization, HashMap<String, ICommand>>();
        HashMap<String, ICommand> commands;

        //Commands
        ICommand acceptCommand = new ChatLinkAcceptCommand();
        ICommand idCommand = new ChatLinkIDCommand();

        //en_US
        commands = new HashMap<String, ICommand>();
        commands.put("accept", acceptCommand);
        commands.put("id", idCommand);
        //TODO: smute, mute, remove, and opt commands
        lcommands.put(Localization.en_US, commands);
    }

    @Override public Mono<?> execute(Message message, CommandData data, String argument) {
        return message.getChannel().flatMap(channel->{
            return message.getAuthorAsMember()
                .flatMap(member->PermissionUtil.check(member, new Permission[] {Permission.MANAGE_CHANNELS}))
                .flatMap(member->{
                    String cmd = ArgumentParser.getCommand(argument);
					String arg = ArgumentParser.getArgument(argument);

                    return lcommands.get(data.locale.locale).getOrDefault(cmd, new InvalidSubcommand()).execute(message, data, arg);
                })
                .cast(Object.class);
        });
    }
    
    @Override public HashMap<String, ICommand> getCommands(Localization locale) { return lcommands.get(locale); }
}

class ChatLinkAcceptCommand implements ICommand {
	@Override public Mono<?> execute(Message message, CommandData data, String argument) {
        return message.getChannel()
            .flatMap(channel->{
				ArgumentParser parser = new ArgumentParser(argument);
				if (parser.couldBeChannelID()) {
					long cid = parser.eatChannelID();
					return ChannelAdapter.of(data.shard, cid).getDocument().getObject(cobj->{
						if (cobj.getOrDefaultObject("data", null)==null) {
							return Mono.error(new Exception());
						}
						return Mono.just(cobj.getOrDefaultObject("data", null).getOrDefaultString("chatlinkid", null));
					}).cast(Long.class).flatMap(clID->{
						if (ChatLinkDocumentCreateStandard.exists(data.bot, clID)) {
							return ChatLinkAdapter.of(data.shard, clID).getDocument().getObject((clobj, cldoc)->{	
								DBArray permitted = clobj.getOrDefaultArray("admins", new DBArray());
								if (!permitted.contains(channel.getId().asLong())) return Mono.error(new LocalizedException(LocalizedString.NotLinkAdmin));
								
		                        return ChannelAdapter.of(data.shard, cid).getDocument().getObject((cobj, cdoc)->{
			    					if (cobj.getOrDefaultString("type", null)!="chatlink"||cobj.getOrDefaultObject("data", null)==null) {
			    						return Mono.error(new LocalizedException(LocalizedString.ChannelNotAwaitingChatlink));
			                        }
		
			                        DBArray links = clobj.getOrCreateArray("links", ()->new DBArray());
			                        links.add(cid);
			                        cldoc.save();
			                        
			                        cobj.getOrDefaultObject("data", null).set("verified", true);
			                        cdoc.save();
			                        
			                        return channel.createMessage(data.locale.localize(LocalizedString.ChatLinkAccepted));
		                        });
							});
						} else {
							return Mono.error(new LocalizedException(LocalizedString.UnrecognizedChatLink));
						}
					});
				} else {
					return Mono.error(new LocalizedException(LocalizedString.UnrecognizedUsage));
				}
			});
	}
}

class ChatLinkIDCommand implements ICommand {
	@Override public Mono<?> execute(Message message, CommandData data, String argument) {
        return message.getChannel()
            .flatMap(channel->{
				return ChannelAdapter.of(data.shard, message.getChannelId().asLong()).getDocument().getObject(cobj->{
					if (cobj.getOrDefaultObject("data", null)==null) {
						return Mono.error(new Exception());
					}
					return Mono.just(cobj.getOrDefaultObject("data", null).getOrDefaultString("chatlinkid", null));
				}).cast(String.class).flatMap(clID->{
					return channel.createMessage("The link ID is "+clID);
				});
			});
	}
}

class ChatLinkOptCommand implements ICommand {
	@Override public Mono<?> execute(Message message, CommandData data, String argument) {
        return message.getChannel()
            .flatMap(channel->{
				ArgumentParser parser = new ArgumentParser(argument);
				if (parser.couldBeChannelID()) {
					long cid = parser.eatChannelID();
					return ChannelAdapter.of(data.shard, cid).getDocument().getObject(cobj->{
						if (cobj.getOrDefaultObject("data", null)==null) {
							return Mono.error(new Exception());
						}
						return Mono.just(cobj.getOrDefaultObject("data", null).getOrDefaultString("chatlinkid", null));
					}).cast(Long.class).flatMap(clID->{
						if (ChatLinkDocumentCreateStandard.exists(data.bot, clID)) {
							return ChatLinkAdapter.of(data.shard, clID).getDocument().getObject((clobj, cldoc)->{	
								DBArray permitted = clobj.getOrDefaultArray("admins", new DBArray());
								if (!permitted.contains(channel.getId().asLong())) return Mono.error(new LocalizedException(LocalizedString.NotLinkAdmin));
								
		                        return ChannelAdapter.of(data.shard, cid).getDocument().getObject((cobj, cdoc)->{
			    					if (cobj.getOrDefaultString("type", null)!="chatlink"||cobj.getOrDefaultObject("data", null)==null) {
			    						return Mono.error(new Exception("Not a chatlink"));
			                        }
			    					
			    					if (!permitted.contains(cid)) permitted.add(cid); //TODO: Force
			    						
			    					return channel.createMessage("ABC");
		                        });
							});
						} else {
							return Mono.error(new LocalizedException(LocalizedString.UnrecognizedChatLink));
						}
					});
				} else {
					return Mono.error(new LocalizedException(LocalizedString.UnrecognizedUsage));
				}
			});
	}
}