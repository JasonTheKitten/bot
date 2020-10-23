package everyos.bot.luwu.mongo.database;

import java.util.Map;
import java.util.WeakHashMap;

import javax.annotation.Nonnull;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import com.mongodb.reactivestreams.client.MongoDatabase;

import everyos.bot.luwu.core.database.DBCollection;
import everyos.bot.luwu.core.database.DBDatabase;
import reactor.core.publisher.Mono;

public class MongoDBDatabase implements DBDatabase {
	MongoDatabase database;
	
	Map<String, MongoDBCollection> collections;
	
	public MongoDBDatabase(@Nonnull String url, @Nonnull String name) {
		MongoClient mongoClient = MongoClients.create(url);
		database = mongoClient.getDatabase(name);
		collections = new WeakHashMap<String, MongoDBCollection>();
	}
	
	@Override public DBCollection collection(@Nonnull String name) {
		if (!collections.containsKey(name)) {
			collections.put(name, new MongoDBCollection(name, database.getCollection(name)));
		}
		return collections.get(name);
	}
	
	@Override public Mono<Void> delete(@Nonnull String collection) {
    	return Mono.from(database.getCollection(collection).drop()).then();
    }
}
