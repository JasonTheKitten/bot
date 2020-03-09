package everyos.discord.bot.channelcase;

import java.util.HashMap;

import discord4j.core.object.entity.Message;
import everyos.discord.bot.adapter.MessageAdapter;
import everyos.discord.bot.commands.ICommand;
import everyos.discord.bot.commands.LocalizedCommandWrapper;
import everyos.discord.bot.commands.channels.ChatLinkCommand;
import everyos.discord.bot.commands.info.PingCommand;
import everyos.discord.bot.commands.info.UptimeCommand;
import everyos.discord.bot.commands.moderation.BanCommand;
import everyos.discord.bot.commands.moderation.KickCommand;
import everyos.discord.bot.localization.Localization;
import everyos.discord.bot.parser.ArgumentParser;
import everyos.storage.database.DBObject;

public class OneWordGameChannelCase implements IChannelCase {
    private static HashMap<String, LocalizedCommandWrapper> commands;
	static {
		commands = new HashMap<String, LocalizedCommandWrapper>();
    }
    static {
		ICommand banCommand = new BanCommand();
        ICommand kickCommand = new KickCommand();
        ICommand onewordCommand = new OneWordManagerCommand();
		
		commands = new HashMap<String, LocalizedCommandWrapper>();
		commands.put("ban", new LocalizedCommandWrapper(banCommand, Localization.en_US));
		commands.put("kick", new LocalizedCommandWrapper(kickCommand, Localization.en_US));
	}

    @Override public void execute(Message message, MessageAdapter adapter) {
        ArgumentParser.ifPrefix(message.getContent().orElse(""), adapter.getPreferredPrefix(), content->{
            String command = ArgumentParser.getCommand(content);
            String argument = ArgumentParser.getArgument(content);
            if (commands.containsKey(command)) {
                commands.get(command).execute(message, adapter, argument);
            } else return false;
            
            return true;
        }).elsedo(()->{
        	adapter.getChannelAdapter(cadapter->{
                DBObject owobj = cadapter.getDocument().getObject();
        		DBObject owcdobj = clobj.getOrDefaultObject("casedata", null);
    			
    		});
        });
        adapter.getChannelAdapter(cadapter->{
            adapter.getMemberAdapter(madapter->{

            });
        });
    }
}