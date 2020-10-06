package everyos.bot.luwu.database;

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
		return Mono.from(collection.replaceOne(Filters.eq("_id", document.getObjectId("_id")), document))
			.flatMap(r->{
				if (r.getMatchedCount()==0) return Mono.from(collection.insertOne(document));
				return Mono.empty();
			}).then();
	}

	public DBObject getObject() {
		return new DBObject(document);
	}
}
