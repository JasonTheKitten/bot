package everyos.discord.bot.commands.moderation;

import java.util.ArrayList;
import java.util.HashMap;

import discord4j.core.object.entity.Message;
import discord4j.core.object.util.Permission;
import everyos.discord.bot.adapter.MessageAdapter;
import everyos.discord.bot.commands.ICommand;
import everyos.discord.bot.localization.Localization;
import everyos.discord.bot.localization.LocalizedString;
import everyos.discord.bot.object.CategoryEnum;
import everyos.discord.bot.parser.ArgumentParser;

public class PurgeCommand implements ICommand {
	@Override public void execute(Message message, MessageAdapter adapter, String argument) {
		adapter.getChannelAdapter(cadapter->{
			adapter.getMemberAdapter(madapter->{
				//madapter.hasPermission(Permission.MANAGE_MESSAGES, hp->{
					/*if (!hp) {
						adapter.formatTextLocale(LocalizedString.InsufficientPermissions, str->cadapter.send(str));
						return;
					}*/
					
					adapter.getTopEntityAdapter(teadapter->{
						ArgumentParser parser = new ArgumentParser(argument);
                        String after = null; int messages = -1; boolean force;
                        if (parser.isNumerical()) {
                            messages = (int) parser.eatNumerical();
                        } else {
                            switch (parser.eat()) {
                                case "all":
                                    break;
                                case "after":
                                    if (parser.isNumerical()) {
                                        after = parser.eat();
                                        break;
                                    }
                                default:
                                    adapter.formatTextLocale(LocalizedString.UnrecognizedUsage, str->cadapter.send(str));
                                    return;
                            }
                        }
                        ArrayList<String> channels = new ArrayList<String>();
                        ArrayList<String> users = new ArrayList<String>();
                        while (!parser.isEmpty()) {
                            if (parser.isNumerical()) {
                                adapter.formatTextLocale(LocalizedString.UnrecognizedUsage, str->cadapter.send(str));
                                return;
                            } else if (parser.couldBeChannelID()) {
                            	String cid = parser.eatChannelID();
                                if (!channels.contains(cid)) channels.add(cid);
                            } else if (parser.couldBeUserID()) {
                            	String uid = parser.eatUserID();
                            	if (!users.contains(uid)) users.add(uid);
                                users.add(uid);
                            } else {
                                String arg = parser.eat();
                                if (arg.equals("--force")||arg.equals("-f")) {
                                    force = true;
                                } else {
                                    adapter.formatTextLocale(LocalizedString.UnrecognizedUsage, str->cadapter.send(str));
                                    return;
                                }
                            }
                        }
                        
                        if (channels.isEmpty()) channels.add(cadapter.getID());
					});
				//});
			});
		});
	}

	@Override public HashMap<String, ICommand> getSubcommands(Localization locale) {
		return null;
	}
	@Override public String getBasicUsage(Localization locale) {
		return null;
	}
	@Override public String getExtendedUsage(Localization locale) {
		return null;
	}
	@Override public CategoryEnum getCategory() {
		return CategoryEnum.Moderation;
	}
}