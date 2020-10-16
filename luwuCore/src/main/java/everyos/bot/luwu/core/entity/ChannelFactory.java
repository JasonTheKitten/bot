package everyos.bot.luwu.core.entity;

import everyos.bot.chat4j.entity.ChatChannel;

public interface ChannelFactory<T extends Channel> {
	public T createChannel(Connection connection, ChatChannel channel);
}