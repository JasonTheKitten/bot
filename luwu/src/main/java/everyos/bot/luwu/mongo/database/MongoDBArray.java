package everyos.bot.luwu.mongo.database;

import java.util.ArrayList;
import java.util.function.Consumer;

import org.bson.Document;

import everyos.bot.luwu.core.database.DBArray;
import everyos.bot.luwu.core.database.DBObject;

public class MongoDBArray implements DBArray {
	protected ArrayList<Object> data;
	
	public MongoDBArray(ArrayList<Object> data) {
		this.data = data;
	}
	public MongoDBArray() {
		this.data = new ArrayList<Object>();
	}
	
	@Override public void forEach(Consumer<Object> func) {
		this.data.forEach(func);
	}
	@Override public boolean contains(Object o) {
		return this.data.contains(o);
	}
	@Override public void add(Object o) {
		this.data.add(o);
	}
	public void add(MongoDBObject o) {
		this.data.add(o.document);
	}
	@Override public void removeFirst(Object o) {
		this.data.remove(o);
	}
	@Override public void removeAll(Object o) {
		while(this.data.remove(o));
	}
	@Override public void remove(int i) {
		data.remove(i);
	}
	@Override public int getLength() {
		return this.data.size();
	}
	@Override public <T> T[] toArray(T[] a) {
		return data.toArray(a);
	}
	public static <T> DBArray from(T[] oarr) {
		MongoDBArray arr = new MongoDBArray();
		for (T obj: oarr) arr.add(obj);
		return arr;
	}
	
	@Override public int getInt(int i) {
		return (int) data.get(i);
	}
	@Override public String getString(int i) {
		return (String) data.get(i);
	}
	@Override public long getLong(int i) {
		return (long) data.get(i);
	}
	@Override public DBObject getObject(int i) {
		return new MongoDBObject((Document) data.get(i));
	}
}
