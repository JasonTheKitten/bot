package everyos.discord.luwu.database;

import org.bson.Document;

import com.mongodb.client.model.Filters;
import com.mongodb.reactivestreams.client.MongoCollection;

import reactor.core.publisher.Mono;

public class DBDocument {
	private MongoCollection<Document> collection;
	private Document document;

	public DBDocument(MongoCollection<Document> collection, Document document) {
		this.collection = collection;
		this.document = document;
	}
	
	public Mono<Void> save() {
		return Mono.from(collection.updateOne(Filters.eq("_id", document.getObjectId("_id")), new Document("$set", document)))
			.flatMap(result->{
				if (result.getMatchedCount()>0) return Mono.just(true);
				return Mono.from(collection.insertOne(document));
			})
			.then();
	}

	public DBObject getObject() {
		return new DBObject(document);
	}
}
