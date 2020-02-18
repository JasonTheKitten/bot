package everyos.discord.bot.channelcase;

import java.util.HashMap;

import discord4j.core.object.entity.Message;
import everyos.discord.bot.adapter.ChannelAdapter;
import everyos.discord.bot.commands.LocalizedCommandWrapper;
import everyos.discord.bot.parser.ArgumentParser;

public class DefaultChannelCase implements IChannelCase {
	private static HashMap<String, LocalizedCommandWrapper> commands;
	static {
		commands = new HashMap<String, LocalizedCommandWrapper>();
	}
	
	@Override public void execute(Message message, ChannelAdapter adapter) {
		ArgumentParser.ifPrefix(message.getContent().orElse(""), adapter.getPreferredPrefix(), prefix->{
			adapter.send("TODO: Commands");
		});
	}
}
