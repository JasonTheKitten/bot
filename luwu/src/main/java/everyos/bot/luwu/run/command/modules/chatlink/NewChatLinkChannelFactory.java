package everyos.bot.luwu.run.command.modules.chatlink;

import everyos.bot.chat4j.entity.ChatChannel;
import everyos.bot.luwu.core.entity.ChannelFactory;
import everyos.bot.luwu.core.entity.Connection;

public class NewChatLinkChannelFactory implements ChannelFactory<NewChatLinkChannel> {
	@Override public NewChatLinkChannel createChannel(Connection connection, ChatChannel channel) {
		return new NewChatLinkChannel(connection, channel);
	}
}
