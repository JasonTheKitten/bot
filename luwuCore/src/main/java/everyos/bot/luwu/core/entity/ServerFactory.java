package everyos.bot.luwu.core.entity;

import java.util.Map;

import everyos.bot.chat4j.entity.ChatGuild;
import everyos.bot.luwu.core.database.DBDocument;
import reactor.core.publisher.Mono;

public interface ServerFactory<T extends Server> {
	Mono<T> create(Connection connection, ChatGuild guild, Map<String, DBDocument> documents);
}
