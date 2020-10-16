package everyos.bot.luwu.mongo.database;

import org.bson.Document;

import com.mongodb.reactivestreams.client.MongoCollection;

import everyos.bot.luwu.core.database.DBCollection;
import everyos.bot.luwu.core.database.DBResult;

public class MongoDBCollection implements DBCollection {
	private MongoCollection<Document> collection;
	
	public MongoDBCollection(String name, MongoCollection<Document> collection) {
		this.collection = collection;
	}
	
	@Override public DBResult scan() {
		return new MongoDBResult(collection);
	}
}
