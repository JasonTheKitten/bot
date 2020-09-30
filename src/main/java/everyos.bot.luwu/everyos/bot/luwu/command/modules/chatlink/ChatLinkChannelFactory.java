package everyos.bot.luwu.command.modules.chatlink;

import everyos.bot.chat4j.entity.ChatChannel;
import everyos.bot.luwu.database.Database;
import everyos.bot.luwu.entity.ChannelFactory;

public class ChatLinkChannelFactory implements ChannelFactory<ChatLinkChannel> {
	@Override public ChatLinkChannel createChannel(ChatChannel channel, Database database) {
		return new ChatLinkChannel(channel, database);
	}
}
