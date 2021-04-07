package everyos.bot.luwu.core.entity;

import java.util.Map;

import everyos.bot.chat4j.entity.ChatUser;
import everyos.bot.luwu.core.database.DBDocument;
import reactor.core.publisher.Mono;

public interface UserFactory<T extends User> {
	Mono<T> create(Connection connection, ChatUser user, Map<String, DBDocument> documents);
}
