package everyos.bot.luwu.core.database;

import java.util.function.Consumer;

import org.bson.conversions.Bson;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface DBResult {
	DBResult with(String key, Object value);
	DBResult filter(Bson filter);
	Mono<Boolean> exists();
	Mono<Void> deleteAll();
	Mono<DBDocument> orDefault(DBObject def);
	Mono<DBDocument> orCreate(Consumer<DBObject> func);
	Mono<DBDocument> orEmpty();
	Flux<DBDocument> rest();
}
