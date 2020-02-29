package everyos.discord.bot.channelcase;

import java.util.HashMap;

import discord4j.core.object.entity.Message;
import everyos.discord.bot.adapter.ChatLinkAdapter;
import everyos.discord.bot.adapter.MessageAdapter;
import everyos.discord.bot.commands.LocalizedCommandWrapper;
import everyos.discord.bot.localization.LocalizedString;
import everyos.discord.bot.parser.ArgumentParser;
import everyos.storage.database.DBObject;

public class ChatLinkChannelCase implements IChannelCase {
	private static HashMap<String, LocalizedCommandWrapper> commands;
	static {
		commands = new HashMap<String, LocalizedCommandWrapper>();
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
        		//TODO: Must check link finished
        		DBObject clcdobj = clobj.getOrDefaultObject("casedata", null);
        		if (!clcdobj.getOrDefaultBoolean("verified", false)) {
        			adapter.formatTextLocale(LocalizedString.UnfinishedChatLinkConnection, str->cadapter.send(str));
        			return;
        		}
    			ChatLinkAdapter cladapter = ChatLinkAdapter.of(cadapter.getID());
    			String msgc = message.getContent().orElse("<No Content>");
    			cladapter.forward(clcdobj.getOrDefaultString("chatlinkid", null), msg->{
    				msg.setContent(msgc); //TODO: Filtering
    				//msg.setEmbed(message.getEmbeds().get(0));
    			});
    		});
        });
	}
}
