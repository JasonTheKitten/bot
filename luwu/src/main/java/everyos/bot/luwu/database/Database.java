package everyos.bot.luwu.database;

import java.util.HashMap;

import javax.annotation.Nonnull;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import com.mongodb.reactivestreams.client.MongoDatabase;
import com.mongodb.reactivestreams.client.Success;

import reactor.core.publisher.Mono;

public class Database {
	MongoDatabase database;
	
	HashMap<String, DBCollection> collections;
	
	public Database(String url, @Nonnull String name) {
		MongoClient mongoClient = MongoClients.create(url);
		database = mongoClient.getDatabase(name);
		collections = new HashMap<String, DBCollection>();
	}
	
	public DBCollection collection(@Nonnull String name) { //TODO: Cache timeouts
		if (!collections.containsKey(name)) {
			collections.put(name, new DBCollection(name, database.getCollection(name)));
		}
			
		return collections.get(name);
	}
	
	public Mono<Success> delete(@Nonnull String collection) {
    	return Mono.from(database.getCollection(collection).drop());
    }

    public static Database of(String url, @Nonnull String name) {
        return new Database(url, name);
    }
}
