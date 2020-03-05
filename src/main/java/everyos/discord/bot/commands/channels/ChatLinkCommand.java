package everyos.discord.bot.commands.channels;

import java.util.HashMap;

import discord4j.core.object.entity.Message;
import discord4j.core.object.util.Permission;
import everyos.discord.bot.Main;
import everyos.discord.bot.adapter.ChannelAdapter;
import everyos.discord.bot.adapter.ChatLinkAdapter;
import everyos.discord.bot.adapter.MessageAdapter;
import everyos.discord.bot.commands.ICommand;
import everyos.discord.bot.localization.Localization;
import everyos.discord.bot.localization.LocalizedString;
import everyos.discord.bot.object.CategoryEnum;
import everyos.discord.bot.parser.ArgumentParser;
import everyos.discord.bot.standards.ChatLinkDocumentCreateStandard;
import everyos.discord.bot.util.FillinUtil;
import everyos.storage.database.DBArray;
import everyos.storage.database.DBDocument;
import everyos.storage.database.DBObject;
import xyz.downgoon.snowflake.Snowflake;

public class ChatLinkCommand implements ICommand {
	private HashMap<Localization, HashMap<String, ICommand>> lcommands;

    public ChatLinkCommand() {
        HashMap<String, ICommand> commands;
        lcommands = new HashMap<Localization, HashMap<String, ICommand>>();

        //Commands
        ICommand createCommand = new ChatLinkCreateCommand();
        ICommand joinCommand = new ChatLinkJoinCommand();
        ICommand acceptCommand = new ChatLinkAcceptCommand();

        //en_US
        commands = new HashMap<String, ICommand>();
        commands.put("create", createCommand);
        commands.put("join", joinCommand);
        commands.put("accept", acceptCommand);
        lcommands.put(Localization.en_US, commands);
    }
	
	@Override public void execute(Message message, MessageAdapter adapter, String argument) {
		adapter.getChannelAdapter(cadapter->{
			adapter.getMemberAdapter(madapter->{
				adapter.getTextLocale(locale->{
					madapter.hasPermission(Permission.MANAGE_CHANNELS, hp->{
						if (!hp) {
							adapter.formatTextLocale(LocalizedString.InsufficientPermissions, str->cadapter.send(str));
							return;
						}
						
						String cmd = ArgumentParser.getCommand(argument);
						String arg = ArgumentParser.getArgument(argument);
						getSubcommands(locale).getOrDefault(cmd, ICommand.invalidSubcommand).execute(message, adapter, arg);;
					});
				});
			});
		});
	}

	@Override public HashMap<String, ICommand> getSubcommands(Localization locale) {
		return lcommands.containsKey(locale)?lcommands.get(locale):lcommands.get(Localization.en_US);
	}

	@Override public String getBasicUsage(Localization locale) {
		return null;
	}

	@Override public String getExtendedUsage(Localization locale) {
		return null;
	}

	@Override public CategoryEnum getCategory() {
		return CategoryEnum.Channels;
	}
}

class ChatLinkJoinCommand implements ICommand {
	@Override public void execute(Message message, MessageAdapter adapter, String argument) {
		adapter.getChannelAdapter(cadapter->{
			adapter.getTextLocale(locale->{
				ArgumentParser parser = new ArgumentParser(argument);
				if (parser.isNumerical()) {
					String clid = String.valueOf(parser.eatNumerical());
					ChatLinkDocumentCreateStandard.ifExists(clid, cladapter->{
						DBDocument cdoc = cadapter.getDocument();
						DBObject cobj = cdoc.getObject();
						cobj.set("type", "chatlink");
						cobj.createObject("casedata", obj->{
							obj.set("chatlinkid", clid);
						});
                        cdoc.save();
                        
                        cadapter.send(adapter.formatTextLocale(locale, LocalizedString.AcceptChatLinkPrompt, FillinUtil.of("id", cadapter.getID())));
					}, ()->{
						cadapter.send(adapter.formatTextLocale(locale, LocalizedString.UnrecognizedChatLink));
					});
				} else {
					cadapter.send(adapter.formatTextLocale(locale, LocalizedString.UnrecognizedUsage));
				}
			});
		});
	}

	@Override public HashMap<String, ICommand> getSubcommands(Localization locale) { return null; }
	@Override public String getBasicUsage(Localization locale) { return null; }
	@Override public String getExtendedUsage(Localization locale) { return null; }
	@Override public CategoryEnum getCategory() { return null; }
}

class ChatLinkCreateCommand implements ICommand {
	@Override public void execute(Message message, MessageAdapter adapter, String argument) {
		adapter.getChannelAdapter(cadapter->{
			adapter.getTextLocale(locale->{
                DBDocument cdoc = cadapter.getDocument();
                DBObject cobj = cdoc.getObject();
                if (cobj.getOrDefaultString("type", "default")!="default") {
                    //TODO: Warning+Confirmation here?
                    adapter.formatTextLocale(LocalizedString.ChannelAlreadyInUse, str->cadapter.send(str));
                    //return;
                }
                
                Snowflake factory = new Snowflake(0, 0);
                String id = String.valueOf(factory.nextId());
                
                cobj.set("type", "chatlink");
                cobj.createObject("casedata", obj->{
                    obj.set("verified", true);
                    obj.set("chatlinkid", id);
                });
                cdoc.save();
                
                ChatLinkAdapter cladapter = ChatLinkAdapter.of(id);
                DBDocument cldoc = cladapter.getDocument();
                DBObject clobj = cldoc.getObject();
                clobj.createArray("admins", arr->{ //TODO: Move to chatlink adapter
                    arr.add(cadapter.getID());
                });
                DBArray links = clobj.getOrCreateArray("links", ()->new DBArray());
                links.add(cadapter.getID());
                cldoc.save();
                
                String ping = "<@"+Main.clientID+">";
                cadapter.send(adapter.formatTextLocale(locale, LocalizedString.ChatLinkOOBE, FillinUtil.of("id", id, "ping", ping)), msg->{
                    msg.pin().subscribe();
                });
			});
		});
	}

	@Override public HashMap<String, ICommand> getSubcommands(Localization locale) { return null; }
	@Override public String getBasicUsage(Localization locale) { return null; }
	@Override public String getExtendedUsage(Localization locale) { return null; }
	@Override public CategoryEnum getCategory() { return null; }
}

class ChatLinkAcceptCommand implements ICommand {
	@Override public void execute(Message message, MessageAdapter adapter, String argument) {
		adapter.getChannelAdapter(cadapter->{
			adapter.getTextLocale(locale->{
				ArgumentParser parser = new ArgumentParser(argument);
				if (parser.couldBeChannelID()) {
					DBDocument ccdoc = cadapter.getDocument();
                    DBObject ccobj = ccdoc.getObject();
					if (!ccobj.getOrDefaultString("type", "default").equals("chatlink")||ccobj.getOrDefaultObject("casedata", null)==null) {
						cadapter.send(adapter.formatTextLocale(locale, LocalizedString.UnrecognizedChatLink));
						return;
					}
					String clid = ccobj.getOrDefaultObject("casedata", null).getOrDefaultString("chatlinkid", null);
                    
					String cid = String.valueOf(parser.eatChannelID());
					ChatLinkDocumentCreateStandard.ifExists(clid, cladapter->{
						/*DBDocument cdoc = cadapter.getDocument();
						DBObject cobj = cdoc.getObject();
						
						DBArray permitted = cobj.getOrDefaultArray("admins", new DBArray());
						if (!permitted.contains(cid)) {
							cadapter.send("Localize this message");
							return;
						}*/
						
                        ChannelAdapter clcadapter = ChannelAdapter.of(cid);
                        DBDocument clcdoc = clcadapter.getDocument();
    					DBObject clcobj = clcdoc.getObject();
    					if (clcobj.getOrDefaultString("type", null)!="chatlink"||clcobj.getOrDefaultObject("casedata", null)==null) {
    						cadapter.send(adapter.formatTextLocale(locale, LocalizedString.ChannelNotAwaitingChatlink));
    						return;
                        }

                        DBDocument cldoc = cladapter.getDocument();
    					DBObject clobj = cldoc.getObject();
                        DBArray links = clobj.getOrCreateArray("links", ()->new DBArray());
                        links.add(cid);
                        cldoc.save();
                        
                        clcobj.getOrDefaultObject("casedata", null).set("verified", true);
                        clcdoc.save();

                        cadapter.send(adapter.formatTextLocale(locale, LocalizedString.ChatLinkAccepted));
					}, ()->{
						cadapter.send(adapter.formatTextLocale(locale, LocalizedString.UnrecognizedChatLink));
					});
				} else {
					cadapter.send(adapter.formatTextLocale(locale, LocalizedString.UnrecognizedUsage));
				}
			});
		});
	}

	@Override public HashMap<String, ICommand> getSubcommands(Localization locale) { return null; }
	@Override public String getBasicUsage(Localization locale) { return null; }
	@Override public String getExtendedUsage(Localization locale) { return null; }
	@Override public CategoryEnum getCategory() { return null; }
}