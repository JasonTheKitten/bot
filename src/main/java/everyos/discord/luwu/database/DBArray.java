package everyos.discord.luwu.database;

import java.util.ArrayList;
import java.util.function.Consumer;

import org.bson.Document;

public class DBArray {
	protected ArrayList<Object> data;
	
	public DBArray(ArrayList<Object> data) {
		this.data = data;
	}
	public DBArray() {
		this.data = new ArrayList<Object>();
	}
	
	public void forEach(Consumer<Object> func) {
		this.data.forEach(func);
	}
	public boolean contains(Object o) {
		return this.data.contains(o);
	}
	public void add(Object o) {
		this.data.add(o);
	}
	public void add(DBObject o) {
		this.data.add(o.document);
	}
	public void removeFirst(Object o) {
		this.data.remove(o);
	}
	public void remove(int i) {
		data.remove(i);
	}
	public int getLength() {
		return this.data.size();
	}
	public <T> T[] toArray(T[] a) {
		return data.toArray(a);
	}
	public static <T> DBArray from(T[] oarr) {
		DBArray arr = new DBArray();
		for (T obj: oarr) arr.add(obj);
		return arr;
	}
	
	public int getInt(int i) {
		return (int) data.get(i);
	}
	public String getString(int i) {
		return (String) data.get(i);
	}
	public long getLong(int i) {
		return (long) data.get(i);
	}
	public DBObject getObject(int i) {
		return new DBObject((Document) data.get(i));
	}
}
