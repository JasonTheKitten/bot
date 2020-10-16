package everyos.bot.luwu.core.database;

import reactor.core.publisher.Mono;

public interface DBDatabase {
	DBCollection collection(String name);
	Mono<Void> delete(String collection);	
}
