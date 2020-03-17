package everyos.discord.bot.command.channel;

import java.util.HashMap;

import discord4j.core.object.entity.Message;
import discord4j.core.object.util.Permission;
import everyos.discord.bot.adapter.ChannelAdapter;
import everyos.discord.bot.adapter.ChatLinkAdapter;
import everyos.discord.bot.command.CommandData;
import everyos.discord.bot.command.ICommand;
import everyos.discord.bot.command.InvalidSubcommand;
import everyos.discord.bot.localization.Localization;
import everyos.discord.bot.localization.LocalizedString;
import everyos.discord.bot.parser.ArgumentParser;
import everyos.discord.bot.standards.ChatLinkDocumentCreateStandard;
import everyos.discord.bot.util.FillinUtil;
import everyos.storage.database.DBArray;
import reactor.core.publisher.Mono;
import xyz.downgoon.snowflake.Snowflake;

public class ChatLinkCommand implements ICommand {
    public HashMap<Localization, HashMap<String, ICommand>> commands;
    public ChatLinkCommand() {
        commands = new HashMap<Localization, HashMap<String, ICommand>>();
        HashMap<String, ICommand> lcommands;

        //Commands
        ICommand createCommand = new ChatLinkCreateCommand();
        ICommand joinCommand = new ChatLinkJoinCommand();
        ICommand acceptCommand = new ChatLinkAcceptCommand();

        //en_US
        lcommands = new HashMap<String, ICommand>();
        lcommands.put("create", createCommand);
        lcommands.put("join", joinCommand);
        lcommands.put("accept", acceptCommand);
        commands.put(Localization.en_US, lcommands);
    }

    @Override public Mono<?> execute(Message message, CommandData data, String argument) {
        return message.getChannel().flatMap(channel->{
            return message.getAuthorAsMember()
                .flatMap(member->member.getBasePermissions())
                .flatMap(perms->{
                    if (!(perms.contains(Permission.ADMINISTRATOR)||perms.contains(Permission.MANAGE_CHANNELS)))
                        return channel.createMessage(data.locale.localize(LocalizedString.InsufficientPermissions));

                    String cmd = ArgumentParser.getCommand(argument);
					String arg = ArgumentParser.getArgument(argument);

                    return commands.get(data.locale.locale).getOrDefault(cmd, new InvalidSubcommand()).execute(message, data, arg);
                });
        });
    }
}

class ChatLinkCreateCommand implements ICommand {
	@Override public Mono<?> execute(Message message, CommandData data, String argument) {
        return message.getChannel()
            .flatMap(channel->{
                return Mono.create(sink->{
                    ChannelAdapter.of(data.shard, channel.getId().asString()).getDocument().getObject((obj, doc)->{
                        if (obj.getOrDefaultString("type", "default")!="default") {
                            //TODO: Warning+Confirmation here?
                            channel.createMessage(data.locale.localize(LocalizedString.ChannelAlreadyInUse)).subscribe();
                            //return;
                        }

                        Snowflake factory = new Snowflake(0, 0);
                        String id = String.valueOf(factory.nextId());
                        
                        obj.set("type", "chatlink");
                        obj.createObject("data", cdobj->{
                            cdobj.set("verified", true);
                            cdobj.set("chatlinkid", id);
                        });
                        doc.save();

                        sink.success(id);
                    });
                }).flatMap(clID->{
                	String cid = channel.getId().asString();
                    ChatLinkAdapter.of(data.shard, (String) clID).getDocument().getObject((clobj, cldoc)->{
                        clobj.createArray("admins", arr->{
                            arr.add(cid);
                        });
                        DBArray links = clobj.getOrCreateArray("links", ()->new DBArray());
                        links.add(cid);
                        cldoc.save();
                    });
                    return channel.createMessage(data.locale.localize(LocalizedString.ChatLinkOOBE,
                    	FillinUtil.of("id", (String) clID, "ping", data.shard.clientID)))
                        .flatMap(msg->msg.pin());
                });
			});
	}
}

class ChatLinkJoinCommand implements ICommand {
	@Override public Mono<?> execute(Message message, CommandData data, String argument) {
        return message.getChannel()
            .flatMap(channel->{
				ArgumentParser parser = new ArgumentParser(argument);
				if (parser.isNumerical()) {
					String clid = String.valueOf(parser.eatNumerical());
					if (ChatLinkDocumentCreateStandard.exists(data.bot, clid)) {
						ChannelAdapter.of(data.shard, channel.getId().asString()).getDocument().getObject((cobj, cdoc)->{
                            cobj.set("type", "chatlink");
                            cobj.createObject("data", obj->{
                                obj.set("chatlinkid", clid);
                            });
                            cdoc.save();
                        });
                        
                        return channel.createMessage(data.locale.localize(LocalizedString.AcceptChatLinkPrompt, FillinUtil.of("id", channel.getId().asString())));
					} else {
						return channel.createMessage(data.locale.localize(LocalizedString.UnrecognizedChatLink));
                    }
				} else {
					return channel.createMessage(data.locale.localize(LocalizedString.UnrecognizedUsage));
				}
		    });
	}
}

class ChatLinkAcceptCommand implements ICommand {
	@Override public Mono<?> execute(Message message, CommandData data, String argument) {
        return message.getChannel()
            .flatMap(channel->{
				ArgumentParser parser = new ArgumentParser(argument);
				if (parser.couldBeChannelID()) {
					String cid = parser.eatChannelID();
					return Mono.create(sink->{
						ChannelAdapter.of(data.shard, cid).getDocument().getObject(cobj->{
							if (!cobj.getOrDefaultString("type", "default").equals("chatlink")||cobj.getOrDefaultObject("data", null)==null) {
								sink.success(channel.createMessage(
										data.locale.localize(LocalizedString.UnrecognizedChatLink)).flatMap(o->Mono.empty()));
								return;
							}
							sink.success(cobj.getOrDefaultObject("data", null).getOrDefaultString("chatlinkid", null));
						});
					}).flatMap(clID->{
						System.out.println(clID);
						return Mono.create(sink->{
							if (ChatLinkDocumentCreateStandard.exists(data.bot, (String) clID)) {
								ChatLinkAdapter.of(data.shard, (String) clID).getDocument().getObject((clobj, cldoc)->{	
									/*DBArray permitted = clobj.getOrDefaultArray("admins", new DBArray());
									if (!permitted.contains(cid)) {
										channel.createMessage("Localize this message").flatMap(o->Mono.empty()).subscribe();
										sink.success();
										return; 
									}*/
									
			                        ChannelAdapter.of(data.shard, cid).getDocument().getObject((cobj, cdoc)->{
				    					if (cobj.getOrDefaultString("type", null)!="chatlink"||cobj.getOrDefaultObject("data", null)==null) {
				    						channel.createMessage(data.locale.localize(LocalizedString.ChannelNotAwaitingChatlink)).subscribe();
				    						return;
				                        }
			
				                        DBArray links = clobj.getOrCreateArray("links", ()->new DBArray());
				                        links.add(cid);
				                        cldoc.save();
				                        
				                        cobj.getOrDefaultObject("data", null).set("verified", true);
				                        cdoc.save();
			                        });
								});
	
		                        channel.createMessage(data.locale.localize(LocalizedString.ChatLinkAccepted)).subscribe();
							} else {
								channel.createMessage(data.locale.localize(LocalizedString.UnrecognizedChatLink)).subscribe();
							};
							sink.success();
						});
					});
				} else {
					return channel.createMessage(data.locale.localize(LocalizedString.UnrecognizedUsage));
				}
			});
	}
}