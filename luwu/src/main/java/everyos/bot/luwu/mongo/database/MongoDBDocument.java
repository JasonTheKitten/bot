package everyos.bot.luwu.mongo.database;

import org.bson.Document;

import com.mongodb.client.model.Filters;
import com.mongodb.reactivestreams.client.MongoCollection;

import everyos.bot.luwu.core.database.DBDocument;
import everyos.bot.luwu.core.database.DBObject;
import reactor.core.publisher.Mono;

public class MongoDBDocument implements DBDocument {
	private MongoCollection<Document> collection;
	private Document document;

	public MongoDBDocument(MongoCollection<Document> collection, Document document) {
		this.collection = collection;
		this.document = document;
	}
	
	@Override public Mono<Void> save() {
		return Mono.from(collection.replaceOne(Filters.eq("_id", document.getObjectId("_id")), document))
			.flatMap(r->{
				if (r.getMatchedCount()==0) return Mono.from(collection.insertOne(document));
				return Mono.empty();
			}).then();
	}

	@Override public DBObject getObject() {
		return new MongoDBObject(document);
	}

	@Override public Mono<Void> delete() {
		return Mono.when(collection.deleteOne(Filters.eq("_id", document.getObjectId("_id"))));
	}
}
