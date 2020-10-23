package everyos.bot.luwu.core.database;

import reactor.core.publisher.Mono;

public interface DBDocument {
	Mono<Void> save();
	DBObject getObject();
	Mono<Void> delete();
}
