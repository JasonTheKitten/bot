package everyos.bot.luwu.mongo.database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Consumer;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.model.Filters;
import com.mongodb.reactivestreams.client.FindPublisher;
import com.mongodb.reactivestreams.client.MongoCollection;

import everyos.bot.luwu.core.database.DBDocument;
import everyos.bot.luwu.core.database.DBObject;
import everyos.bot.luwu.core.database.DBResult;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class MongoDBResult implements DBResult {
	private HashMap<String, Object> iddata;
	private FindPublisher<Document> search;
	private MongoCollection<Document> collection;
	private ArrayList<Bson> filters;
	
	
	public MongoDBResult(MongoCollection<Document> collection) {
		this.iddata = new HashMap<String, Object>();
		this.collection = collection;
		this.search = collection.find();
		this.filters = new ArrayList<Bson>();
	}
	
	@Override
	public DBResult with(String key, Object value) {
		iddata.put(key, value);
		filters.add(Filters.eq(key, value));
		return this;
	}
	@Override
	public DBResult filter(Bson filter) {
		filters.add(filter);
		return this;
	}
	
	@Override
	public Mono<Boolean> exists() {
		return Mono.from(search().first()).map(o->true).defaultIfEmpty(false);
	}
	@Override
	public Mono<Void> deleteAll() {
		return Mono.from(collection.deleteMany(Filters.and(filters.toArray(new Bson[filters.size()])))).then();
	}

	@Override
	public Mono<DBDocument> orDefault(DBObject def) {
		return Mono.from(search().first()).map(d->new MongoDBDocument(collection, d)); //TODO
	}
	@Override
	public Mono<DBDocument> orCreate(Consumer<DBObject> func) {
		return Mono.from(search().first()).map(d->new MongoDBDocument(collection, d))
			.switchIfEmpty(Mono.just(new MongoDBDocument(collection, new Document())).doOnNext(document->{
				iddata.forEach((k, v)->{
					document.getObject().set(k, v);
				});
				func.accept(document.getObject());
			}))
			.cast(DBDocument.class);
	}
	@Override
	public Mono<DBDocument> orEmpty() {
		return Mono.from(search().first()).map(d->new MongoDBDocument(collection, d));
	}

	@Override
	public Flux<DBDocument> rest() {
		return Flux.from(search()).map(d->new MongoDBDocument(collection, d));
	}
	
	private FindPublisher<Document> search() {
		return search.filter(Filters.and(filters.toArray(new Bson[filters.size()])));
	}
}
