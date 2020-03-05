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
import everyos.discord.bot.localization.LocalizedString;
import everyos.discord.bot.parser.ArgumentParser;
import everyos.discord.bot.standards.ChatLinkDocumentCreateStandard;
import everyos.storage.database.DBObject;

public class ChatLinkChannelCase implements IChannelCase {
	private static HashMap<String, LocalizedCommandWrapper> commands;
	static {
		commands = new HashMap<String, LocalizedCommandWrapper>();
    }
    static {
		ICommand banCommand = new BanCommand();
		ICommand kickCommand = new KickCommand();
		ICommand uptimeCommand = new UptimeCommand();
		ICommand chatlinkCommand = new ChatLinkCommand();
		ICommand pingCommand = new PingCommand();
		
		commands = new HashMap<String, LocalizedCommandWrapper>();
		commands.put("ban", new LocalizedCommandWrapper(banCommand, Localization.en_US));
		commands.put("kick", new LocalizedCommandWrapper(kickCommand, Localization.en_US));
		commands.put("uptime", new LocalizedCommandWrapper(uptimeCommand, Localization.en_US));
		commands.put("link", new LocalizedCommandWrapper(chatlinkCommand, Localization.en_US));
		commands.put("ping", new LocalizedCommandWrapper(pingCommand, Localization.en_US));
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
                DBObject clobj = cadapter.getDocument().getObject();
        		if (!(clobj.has("casedata")&&clobj.getOrDefaultObject("casedata", null).has("chatlinkid"))) {
        			return; //TODO
                }
        		DBObject clcdobj = clobj.getOrDefaultObject("casedata", null);
        		if (!clcdobj.getOrDefaultBoolean("verified", false)) {
        			adapter.formatTextLocale(LocalizedString.UnfinishedChatLinkConnection, str->cadapter.send(str));
        			return;
                }
    			ChatLinkDocumentCreateStandard.ifExists(clcdobj.getOrDefaultString("chatlinkid", null), cladapter->{
                    String msgc = message.getContent().orElse("<No Content>");
                    cladapter.forward(cadapter.getID(), msg->{
                        msg.setContent(msgc); //TODO: Filtering
                        //msg.setEmbed(message.getEmbeds().get(0));
                    });
                }, ()->{});
    		});
        });
	}
}
