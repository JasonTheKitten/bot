package everyos.bot.luwu.run.command.modules.chatlink;

import everyos.bot.chat4j.entity.ChatChannel;
import everyos.bot.luwu.core.entity.ChannelFactory;
import everyos.bot.luwu.core.entity.Connection;

public class ChatLinkChannelFactory implements ChannelFactory<ChatLinkChannel> {
	@Override public ChatLinkChannel createChannel(Connection connection, ChatChannel channel) {
		return new ChatLinkChannel(connection, channel);
	}
}
