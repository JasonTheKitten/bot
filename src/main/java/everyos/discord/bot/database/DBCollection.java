package everyos.discord.bot.database;

import org.bson.Document;

import com.mongodb.reactivestreams.client.MongoCollection;

public class DBCollection {
	private MongoCollection<Document> collection;
	
	//private HashMap<Long, DBDocument> documents;

	public DBCollection(String name, MongoCollection<Document> collection) {
		this.collection = collection;
		//documents = new HashMap<Long, DBDocument>();
	}
	
	public String getName() {
		return collection.getNamespace().getCollectionName();
	}
	
	/*public Mono<Boolean> has(String name) {
		return Mono.from(collection.find(Filters.eq("id", name)).first()).map(u->true).defaultIfEmpty(false);
	}*/
	
	/*public Mono<DBDocument> getOrEmpty(long id) {//TODO: Sync
		if (documents.containsKey(id)) return Mono.just(documents.get(id));
		return Mono.from(collection.find(Filters.eq("id", id)).first()).map(document->{
			DBDocument doc = new DBDocument(collection, document);
			documents.put(id, doc);
			return doc;
		});
	}
	public Mono<DBDocument> getOrCreate(long id) {
		return getOrEmpty(id).switchIfEmpty(
			Mono.when(collection.insertOne(new Document().append("id", id)))
			.then(getOrEmpty(id)));
	}

	public Mono<DBDocument> getOrSet(long id, Consumer<DBObject> func) {
		return null; //TODO:
	}
	public Mono<DBDocument> getOrSet(long id, BiConsumer<DBObject, DBDocument> func) {
		return null; //TODO:
	}*/

	public DBResult scan() {
		return new DBResult(collection);
	}
}
