package everyos.bot.luwu.core.entity;

import java.util.Map;

import everyos.bot.chat4j.entity.ChatMessage;
import everyos.bot.luwu.core.database.DBDocument;
import reactor.core.publisher.Mono;

public interface MessageFactory<T extends Message> {
	Mono<T> createMessage(Connection connection, ChatMessage message, Map<String, DBDocument> documents);
}
