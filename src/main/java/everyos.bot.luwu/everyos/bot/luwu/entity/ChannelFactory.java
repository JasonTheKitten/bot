package everyos.bot.luwu.entity;

import everyos.bot.chat4j.entity.ChatChannel;
import everyos.bot.luwu.database.Database;

public interface ChannelFactory<T extends Channel> {
	public T createChannel(ChatChannel channel, Database database);
}