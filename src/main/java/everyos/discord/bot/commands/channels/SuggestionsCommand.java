package everyos.discord.bot.commands.channels;

import java.util.HashMap;

import discord4j.core.object.entity.Message;
import discord4j.core.object.util.Permission;
import everyos.discord.bot.adapter.MessageAdapter;
import everyos.discord.bot.commands.ICommand;
import everyos.discord.bot.localization.Localization;
import everyos.discord.bot.localization.LocalizedString;
import everyos.discord.bot.object.CategoryEnum;
import everyos.discord.bot.parser.ArgumentParser;
import everyos.storage.database.DBDocument;
import everyos.storage.database.DBObject;

public class SuggestionsCommand implements ICommand {
    @Override public void execute(Message message, MessageAdapter adapter, String argument) {
        adapter.getChannelAdapter(cadapter->{ //TODO: Check permissions
			adapter.getTextLocale(locale->{
                adapter.getMemberAdapter(madapter->{
                    madapter.hasPermission(Permission.MANAGE_CHANNELS, hp->{
                        if (!hp) {
                            cadapter.send(adapter.formatTextLocale(locale, LocalizedString.InsufficientPermissions));
                            return;
                        }

                        ArgumentParser parser = new ArgumentParser(argument);
                        DBDocument cdoc = cadapter.getDocument();
                        DBObject cobj = cdoc.getObject();
                        cobj.createObject("casedata", obj->{ //TODO: Only allow channels in same guild
                            if (parser.couldBeChannelID()) {
                                obj.set("votechannel", cadapter.getID());
                            } else if (!parser.isEmpty()) {
                                cadapter.send(adapter.formatTextLocale(locale, LocalizedString.UnrecognizedUsage));
                                return;
                            } else obj.set("votechannel", cadapter.getID());
                        });
                        cobj.set("type", "suggestion");
                        cdoc.save();
                        
                        cadapter.send(adapter.formatTextLocale(locale, LocalizedString.ChannelsSet));
                    });
                });
			});
		});
    }

    @Override public HashMap<String, ICommand> getSubcommands(Localization locale) { return null; }
    @Override public String getBasicUsage(Localization locale) { return null; }
    @Override public String getExtendedUsage(Localization locale) { return null; }

    @Override public CategoryEnum getCategory() {
        return CategoryEnum.Channels;
    }
}