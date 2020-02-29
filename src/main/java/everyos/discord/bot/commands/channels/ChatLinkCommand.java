package everyos.discord.bot.commands.channels;

import java.util.HashMap;

import discord4j.core.object.entity.Message;
import discord4j.core.object.util.Permission;
import everyos.discord.bot.adapter.ChannelAdapter;
import everyos.discord.bot.adapter.MessageAdapter;
import everyos.discord.bot.commands.ICommand;
import everyos.discord.bot.localization.Localization;
import everyos.discord.bot.localization.LocalizedString;
import everyos.discord.bot.object.CategoryEnum;
import everyos.discord.bot.parser.ArgumentParser;
import everyos.discord.bot.standards.ChatLinkDocumentCreateStandard;
import everyos.discord.bot.util.FillinUtil;
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

        //en_US
        commands = new HashMap<String, ICommand>();
        commands.put("create", createCommand);
        commands.put("join", joinCommand);
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
					String cid = String.valueOf(parser.eatNumerical());
					ChatLinkDocumentCreateStandard.ifExists(cid, cladapter->{
						DBDocument cldoc = cadapter.getDocument();
						DBObject clobj = cldoc.getObject();
						clobj.set("type", "chatlink");
						clobj.createObject("casedata", obj->{
							obj.set("chatlinkid", cid);
						});
						cldoc.save();
					}, ()->{
						
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
				ArgumentParser parser = new ArgumentParser(argument);
				if (parser.couldBeChannelID()) {
					String cid = parser.eatChannelID();
					ChannelAdapter clcadapter = ChannelAdapter.of(cid);
					DBDocument cldoc = clcadapter.getDocument();
					DBObject clobj = cldoc.getObject();
					if (clobj.getOrDefaultString("type", "default")!="default") {
						//TODO: Warning+Confirmation here?
						adapter.formatTextLocale(LocalizedString.ChannelAlreadyInUse, str->cadapter.send(str));
						//return;
					}
					
					Snowflake factory = new Snowflake(0, 0);
					String id = String.valueOf(factory.nextId());
					
					clobj.set("type", "chatlink");
					clobj.createObject("casedata", obj->{
						obj.set("verified", true);
						obj.createArray("admins", arr->{
							arr.add(cadapter.getID());
						});
						obj.set("chatlinkid", id);
					});
					cldoc.save();
					
					cadapter.send(adapter.formatTextLocale(locale, LocalizedString.ChatLinkCreate, FillinUtil.of("id", id)));
					clcadapter.send(adapter.formatTextLocale(locale, LocalizedString.ChatLinkOOBE), msg->{
						msg.pin().subscribe();
					});
				} else {
					adapter.formatTextLocale(LocalizedString.UnrecognizedUsage, str->cadapter.send(str));
				}
			});
		});
	}

	@Override public HashMap<String, ICommand> getSubcommands(Localization locale) { return null; }
	@Override public String getBasicUsage(Localization locale) { return null; }
	@Override public String getExtendedUsage(Localization locale) { return null; }
	@Override public CategoryEnum getCategory() { return CategoryEnum.Channels; }
}