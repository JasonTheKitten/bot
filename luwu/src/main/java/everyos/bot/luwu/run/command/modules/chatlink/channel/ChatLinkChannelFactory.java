package everyos.bot.luwu.run.command.modules.chatlink.channel;

import java.util.Map;

import everyos.bot.chat4j.entity.ChatChannel;
import everyos.bot.luwu.core.database.DBDocument;
import everyos.bot.luwu.core.entity.ChannelFactory;
import everyos.bot.luwu.core.entity.Connection;
import reactor.core.publisher.Mono;

public class ChatLinkChannelFactory implements ChannelFactory<ChatLinkChannel> {
	@Override public Mono<ChatLinkChannel> createChannel(Connection connection, ChatChannel channel, Map<String, DBDocument> documents) {
		return Mono.just(new ChatLinkChannel(connection, channel, documents));
	}
}
