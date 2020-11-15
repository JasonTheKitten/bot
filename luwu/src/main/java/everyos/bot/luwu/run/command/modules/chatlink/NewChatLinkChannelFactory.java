package everyos.bot.luwu.run.command.modules.chatlink;

import everyos.bot.chat4j.entity.ChatChannel;
import everyos.bot.luwu.core.database.DBDocument;
import everyos.bot.luwu.core.entity.ChannelFactory;
import everyos.bot.luwu.core.entity.Connection;
import reactor.core.publisher.Mono;

public class NewChatLinkChannelFactory implements ChannelFactory<NewChatLinkChannel> {
	@Override public Mono<NewChatLinkChannel> createChannel(Connection connection, ChatChannel channel, DBDocument document) {
		return Mono.just(new NewChatLinkChannel(connection, channel, document));
	}
}
