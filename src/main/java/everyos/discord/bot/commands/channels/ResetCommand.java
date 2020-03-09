package everyos.discord.bot.commands.channels;

import java.util.HashMap;
import java.util.function.Consumer;

import discord4j.core.object.entity.Message;
import discord4j.core.object.util.Permission;
import everyos.discord.bot.adapter.ChannelAdapter;
import everyos.discord.bot.adapter.MessageAdapter;
import everyos.discord.bot.commands.ICommand;
import everyos.discord.bot.localization.Localization;
import everyos.discord.bot.localization.LocalizedString;
import everyos.discord.bot.object.CategoryEnum;
import everyos.discord.bot.parser.ArgumentParser;
import everyos.discord.bot.standards.ChannelDocumentCreateStandard;
import everyos.storage.database.DBDocument;
import everyos.storage.database.DBObject;

public class ResetCommand implements ICommand {
	@Override public void execute(Message message, MessageAdapter adapter, String argument) {
		adapter.getChannelAdapter(cadapter->{ //TODO: Check permissions
			adapter.getTextLocale(locale->{
                adapter.getMemberAdapter(madapter->{
                    madapter.hasPermission(Permission.MANAGE_CHANNELS, hp->{
                        if (!hp) {
                            cadapter.send(adapter.formatTextLocale(locale, LocalizedString.InsufficientPermissions));
                            return;
                        }

                        Consumer<ChannelAdapter> resetChannel = channel->{
                            //TODO: Manage any chatlink connections
                            DBDocument cdoc = channel.getDocument();
                            DBObject cobj = cdoc.getObject();
                            cobj.remove("casedata");
                            cobj.remove("type");
                            cdoc.save();
                            cadapter.send(adapter.formatTextLocale(locale, LocalizedString.ChannelsSet));
                        };

                        ArgumentParser parser = new ArgumentParser(argument);
                        if (parser.isEmpty()) {
                            resetChannel.accept(cadapter);
                        } else if (parser.couldBeChannelID()) {
                            ChannelDocumentCreateStandard.ifExists(parser.eatChannelID(), channel->{
                                resetChannel.accept(channel);
                            }, ()->{
                                cadapter.send(adapter.formatTextLocale(locale, LocalizedString.UnrecognizedChannel));
                            });
                        } else {
                            cadapter.send(adapter.formatTextLocale(locale, LocalizedString.UnrecognizedUsage));
                        }
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
    
    public static void releaseOldChannel(ChannelAdapter cadapter) {

    }
}
