package everyos.bot.luwu.core.entity;

import java.util.Map;

import everyos.bot.chat4j.entity.ChatChannel;
import everyos.bot.luwu.core.database.DBDocument;
import reactor.core.publisher.Mono;

public interface ChannelFactory<T extends Channel> {
	Mono<T> createChannel(Connection connection, ChatChannel channel, Map<String, DBDocument> documents);
}