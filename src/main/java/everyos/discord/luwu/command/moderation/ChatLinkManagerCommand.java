package everyos.discord.luwu.command.moderation;

import java.util.HashMap;

import discord4j.core.object.entity.Message;
import discord4j.rest.util.Permission;
import everyos.discord.luwu.adapter.ChannelAdapter;
import everyos.discord.luwu.adapter.ChatLinkAdapter;
import everyos.discord.luwu.command.CommandData;
import everyos.discord.luwu.command.ICommand;
import everyos.discord.luwu.command.IGroupCommand;
import everyos.discord.luwu.database.DBArray;
import everyos.discord.luwu.database.DBObject;
import everyos.discord.luwu.localization.Localization;
import everyos.discord.luwu.localization.LocalizedString;
import everyos.discord.luwu.parser.ArgumentParser;
import everyos.discord.luwu.standards.ChatLinkDocumentCreateStandard;
import everyos.discord.luwu.util.PermissionUtil;
import everyos.discord.luwu.util.ErrorUtil.LocalizedException;
import reactor.core.publisher.Mono;

public class ChatLinkManagerCommand implements IGroupCommand {
	public HashMap<Localization, HashMap<String, ICommand>> lcommands;
    public ChatLinkManagerCommand() {
        lcommands = new HashMap<Localization, HashMap<String, ICommand>>();
        HashMap<String, ICommand> commands;

        //Commands
        ICommand acceptCommand = new ChatLinkAcceptCommand();
        ICommand idCommand = new ChatLinkIDCommand();
        ICommand optCommand = new ChatLinkOptCommand();
        ICommand gmuteCommand = new ChatLinkGMuteCommand();
        ICommand ungmuteCommand = new ChatLinkUngmuteCommand();

        //en_US
        commands = new HashMap<String, ICommand>();
        commands.put("accept", acceptCommand);
        commands.put("id", idCommand);
        commands.put("opt", optCommand);
        commands.put("gmute", gmuteCommand);
        commands.put("ungmute", ungmuteCommand);
        //TODO: mute and remove commands
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
					return Mono.just(obj.getOrDefaultObject("data", null).getOrDefaultLong("chatlinkid", -1L));
				}).flatMap(clID->{
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
						return Mono.just(obj.getOrDefaultObject("data", null).getOrDefaultLong("chatlinkid", -1L));
					}).cast(Long.class).flatMap(clID->{
						return ChatLinkDocumentCreateStandard.exists(data.bot, clID).flatMap(exists->{
							if (!exists) return Mono.error(new LocalizedException(LocalizedString.UnrecognizedChatLink));
							return ChatLinkAdapter.of(data.bot, clID).getDocument().flatMap(cldoc->{
								DBObject clobj = cldoc.getObject();
								DBArray permitted = clobj.getOrCreateArray("admins", ()->new DBArray());
								if (!permitted.contains(channel.getId().asLong())) return Mono.error(new LocalizedException(LocalizedString.NotLinkAdmin));
								
		                        return ChannelAdapter.of(data.bot, cid).getDocument().flatMap(cdoc->{
		                        	DBObject cobj = cdoc.getObject();
		                        	if (!cobj.getOrDefaultString("type", null).equals("chatlink")||cobj.getOrDefaultObject("data", null)==null)
			    						return Mono.error(new LocalizedException(LocalizedString.NotLinkChannel));
			    					
			    					if (!permitted.contains(cid)) permitted.add(cid); //TODO: Force
			    						
			    					return cldoc.save().then(channel.createMessage(data.localize(LocalizedString.LinkChannelOpted)));
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

class ChatLinkGMuteCommand implements ICommand {
	@Override public Mono<?> execute(Message message, CommandData data, String argument) {
        return message.getChannel()
            .flatMap(channel->{
				ArgumentParser parser = new ArgumentParser(argument);
				if (parser.couldBeChannelID()) {
					long uid = parser.eatChannelID();
					return ChannelAdapter.of(data.bot, message.getChannelId().asLong()).getDocument().map(doc->doc.getObject()).flatMap(obj->{
						if (obj.getOrDefaultObject("data", null)==null) return Mono.error(new Exception());
						return Mono.just(obj.getOrDefaultObject("data", null).getOrDefaultLong("chatlinkid", -1L));
					}).cast(Long.class).flatMap(clID->{
						return ChatLinkDocumentCreateStandard.exists(data.bot, clID).flatMap(exists->{
							if (!exists) return Mono.error(new LocalizedException(LocalizedString.UnrecognizedChatLink));
							return ChatLinkAdapter.of(data.bot, clID).getDocument().flatMap(cldoc->{
								DBObject clobj = cldoc.getObject();
								
								DBArray permitted = clobj.getOrDefaultArray("admins", new DBArray());
								if (!permitted.contains(channel.getId().asLong())) return Mono.error(new LocalizedException(LocalizedString.NotLinkAdmin));
								
								DBArray muted = clobj.getOrCreateArray("muted", ()->new DBArray());
								if (muted.contains(uid)) return Mono.error(new LocalizedException(LocalizedString.UserAlreadyGloballyMuted));
								muted.add(uid);
			    				return cldoc.save().then(channel.createMessage(data.localize(LocalizedString.LinkUserGloballyMuted)));
							});
						});
					});
				} else {
					return Mono.error(new LocalizedException(LocalizedString.UnrecognizedUsage));
				}
			});
	}
}

class ChatLinkUngmuteCommand implements ICommand {
	@Override public Mono<?> execute(Message message, CommandData data, String argument) {
        return message.getChannel()
            .flatMap(channel->{
				ArgumentParser parser = new ArgumentParser(argument);
				if (parser.couldBeChannelID()) {
					long uid = parser.eatChannelID();
					return ChannelAdapter.of(data.bot, message.getChannelId().asLong()).getDocument().map(doc->doc.getObject()).flatMap(obj->{
						if (obj.getOrDefaultObject("data", null)==null) return Mono.error(new Exception());
						return Mono.just(obj.getOrDefaultObject("data", null).getOrDefaultLong("chatlinkid", -1L));
					}).cast(Long.class).flatMap(clID->{
						return ChatLinkDocumentCreateStandard.exists(data.bot, clID).flatMap(exists->{
							if (!exists) return Mono.error(new LocalizedException(LocalizedString.UnrecognizedChatLink));
							return ChatLinkAdapter.of(data.bot, clID).getDocument().flatMap(cldoc->{
								DBObject clobj = cldoc.getObject();
								
								DBArray permitted = clobj.getOrDefaultArray("admins", new DBArray());
								if (!permitted.contains(channel.getId().asLong())) return Mono.error(new LocalizedException(LocalizedString.NotLinkAdmin));
								
								DBArray muted = clobj.getOrCreateArray("muted", ()->new DBArray());
								if (!muted.contains(uid)) return Mono.error(new LocalizedException(LocalizedString.UserNotGloballyMuted));
								muted.removeFirst(uid);
			    				return cldoc.save().then(channel.createMessage(data.localize(LocalizedString.LinkUserGloballyUnmuted)));
							});
						});
					});
				} else {
					return Mono.error(new LocalizedException(LocalizedString.UnrecognizedUsage));
				}
			});
	}
}