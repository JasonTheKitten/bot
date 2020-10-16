package everyos.bot.luwu.mongo.database;

import org.bson.Document;

import com.mongodb.client.model.Filters;
import com.mongodb.reactivestreams.client.MongoCollection;

import everyos.bot.luwu.core.database.DBDocument;
import reactor.core.publisher.Mono;

public class MongoDBDocument implements DBDocument {
	private MongoCollection<Document> collection;
	private Document document;

	public MongoDBDocument(MongoCollection<Document> collection, Document document) {
		this.collection = collection;
		this.document = document;
	}
	
	public Mono<Void> save() {
		return Mono.from(collection.replaceOne(Filters.eq("_id", document.getObjectId("_id")), document))
			.flatMap(r->{
				if (r.getMatchedCount()==0) return Mono.from(collection.insertOne(document));
				return Mono.empty();
			}).then();
	}

	public MongoDBObject getObject() {
		return new MongoDBObject(document);
	}
}
