package everyos.discord.bot.channelcase;

import discord4j.core.object.entity.Message;
import everyos.discord.bot.adapter.MessageAdapter;

public interface IChannelCase {
	void execute(Message message, MessageAdapter madapter);
}
