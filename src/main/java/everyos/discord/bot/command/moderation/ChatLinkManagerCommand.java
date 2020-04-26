package everyos.discord.bot.command.moderation;

import java.util.HashMap;

import discord4j.core.object.entity.Message;
import discord4j.rest.util.Permission;
import everyos.discord.bot.adapter.ChannelAdapter;
import everyos.discord.bot.adapter.ChatLinkAdapter;
import everyos.discord.bot.command.CommandData;
import everyos.discord.bot.command.ICommand;
import everyos.discord.bot.command.IGroupCommand;
import everyos.discord.bot.database.DBArray;
import everyos.discord.bot.database.DBObject;
import everyos.discord.bot.localization.Localization;
import everyos.discord.bot.localization.LocalizedString;
import everyos.discord.bot.parser.ArgumentParser;
import everyos.discord.bot.standards.ChatLinkDocumentCreateStandard;
import everyos.discord.bot.util.ErrorUtil.LocalizedException;
import everyos.discord.bot.util.PermissionUtil;
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
                	if (argument.equals("")) {}; //TODO: Show help instead

                    String cmd = ArgumentParser.getCommand(argument);
                    String arg = ArgumentParser.getArgument(argument);
                    
                    ICommand command = lcommands.get(data.locale.locale).get(cmd);
                    
                    if (command==null)
                    	return message.getChannel().flatMap(c->c.createMessage(data.localize(LocalizedString.NoSuchSubcommand)));
            	    
                    return command.execute(message, data, arg);
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
					return ChannelAdapter.of(data.bot, cid).getDocument().map(doc->doc.getObject()).flatMap(obj->{
						if (obj.getOrDefaultObject("data", null)==null) {
							return Mono.error(new Exception());
						}
						return Mono.just(obj.getOrDefaultObject("data", null).getOrDefaultLong("chatlinkid", -1L));
					}).cast(Long.class).flatMap(clID->{
						return ChatLinkDocumentCreateStandard.exists(data.bot, clID).flatMap(exists->{
							if (!exists) return Mono.error(new LocalizedException(LocalizedString.UnrecognizedChatLink));
							return ChatLinkAdapter.of(data.bot, clID).getDocument();
						}).flatMap(cldoc->{
							DBObject clobj = cldoc.getObject();	
							DBArray permitted = clobj.getOrDefaultArray("admins", new DBArray());
							if (!permitted.contains(channel.getId().asLong())) return Mono.error(new LocalizedException(LocalizedString.NotLinkAdmin));
							
	                        return ChannelAdapter.of(data.bot, cid).getDocument().flatMap(cdoc->{
	                        	DBObject cobj = cdoc.getObject();
		    					if (!cobj.getOrDefaultString("type", null).equals("chatlink")||cobj.getOrDefaultObject("data", null)==null)
		    						return Mono.error(new LocalizedException(LocalizedString.ChannelNotAwaitingChatlink));
	
		                        DBArray links = clobj.getOrCreateArray("links", ()->new DBArray());
		                        if (!links.contains(cid)) links.add(cid);
		                        
		                        cobj.getOrDefaultObject("data", null).set("verified", true);
		                        
		                        return cldoc.save().and(cdoc.save()).then(channel.createMessage(data.localize(LocalizedString.ChatLinkAccepted)));
	                        });
						});
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
				return ChannelAdapter.of(data.bot, message.getChannelId().asLong()).getDocument().map(doc->doc.getObject()).flatMap(obj->{
					if (obj.getOrDefaultObject("data", null)==null) return Mono.error(new Exception());
					return Mono.just(obj.getOrDefaultObject("data", null).getOrDefaultString("chatlinkid", null));
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
					return ChannelAdapter.of(data.bot, message.getChannelId().asLong()).getDocument().map(doc->doc.getObject()).flatMap(obj->{
						if (obj.getOrDefaultObject("data", null)==null) return Mono.error(new Exception());
						return Mono.just(obj.getOrDefaultObject("data", null).getOrDefaultString("chatlinkid", null));
					}).cast(Long.class).flatMap(clID->{
						return ChatLinkDocumentCreateStandard.exists(data.bot, clID).flatMap(exists->{
							if (!exists) return Mono.error(new LocalizedException(LocalizedString.UnrecognizedChatLink));
							return ChatLinkAdapter.of(data.bot, clID).getDocument().flatMap(cldoc->{
								DBObject clobj = cldoc.getObject();
								DBArray permitted = clobj.getOrDefaultArray("admins", new DBArray());
								if (!permitted.contains(channel.getId().asLong())) return Mono.error(new LocalizedException(LocalizedString.NotLinkAdmin));
								
		                        return ChannelAdapter.of(data.bot, cid).getDocument().flatMap(cdoc->{
		                        	DBObject cobj = cdoc.getObject();
			    					if (cobj.getOrDefaultString("type", null)!="chatlink"||cobj.getOrDefaultObject("data", null)==null)
			    						return Mono.error(new Exception("Not a chatlink"));
			    					
			    					if (!permitted.contains(cid)) permitted.add(cid); //TODO: Force
			    						
			    					return channel.createMessage("ABC");
		                        });
							});
						});
					});
				} else {
					return Mono.error(new LocalizedException(LocalizedString.UnrecognizedUsage));
				}
			});
	}
}