package everyos.discord.bot.channelcase;

import discord4j.core.object.entity.Message;
import everyos.discord.bot.adapter.ChannelAdapter;

public interface IChannelCase {
	void execute(Message message, ChannelAdapter object);
}
