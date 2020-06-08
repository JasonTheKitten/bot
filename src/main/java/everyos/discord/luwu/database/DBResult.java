package everyos.discord.luwu.database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Consumer;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.reactivestreams.client.FindPublisher;
import com.mongodb.reactivestreams.client.MongoCollection;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class DBResult {
	private HashMap<String, Object> iddata;
	private FindPublisher<Document> search;
	private MongoCollection<Document> collection;
	private ArrayList<Bson> filters;
	
	
	public DBResult(MongoCollection<Document> collection) {
		this.iddata = new HashMap<String, Object>();
		this.collection = collection;
		this.search = collection.find();
		this.filters = new ArrayList<Bson>();
	}
	
	public DBResult with(String key, Object value) {
		iddata.put(key, value);
		filters.add(Filters.eq(key, value));
		return this;
	}
	public DBResult filter(Bson filter) {
		filters.add(filter);
		return this;
	}
	
	public Mono<Boolean> exists() {
		return Mono.from(search().first()).map(o->true).defaultIfEmpty(false);
	}
	public Mono<DeleteResult> deleteAll() {
		return Mono.from(collection.deleteMany(Filters.and(filters.toArray(new Bson[filters.size()]))));
	}

	public Mono<DBDocument> orDefault(DBObject def) {
		return Mono.from(search().first()).map(d->new DBDocument(collection, d));
	}
	public Mono<DBDocument> orSet(Consumer<DBObject> func) {
		return Mono.from(search().first()).map(d->new DBDocument(collection, d))
			.switchIfEmpty(Mono.just(new DBDocument(collection, new Document())).doOnNext(document->{
				iddata.forEach((k, v)->{
					document.getObject().set(k, v);
				});
			}));
	}
	public Mono<DBDocument> orCreate(Consumer<DBObject> func) {
		return Mono.from(search().first()).map(d->new DBDocument(collection, d));
	}
	public Mono<DBDocument> orEmpty() {
		return Mono.from(search().first()).map(d->new DBDocument(collection, d));
	}

	public Flux<DBDocument> rest() {
		return Flux.from(search()).map(d->new DBDocument(collection, d));
	}
	
	private FindPublisher<Document> search() {
		return search.filter(Filters.and(filters.toArray(new Bson[filters.size()])));
	}
}
