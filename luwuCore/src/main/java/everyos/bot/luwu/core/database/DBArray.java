package everyos.bot.luwu.core.database;

import java.util.function.Consumer;

public interface DBArray {
	void forEach(Consumer<Object> func);
	boolean contains(Object o);
	void add(Object o);
	void removeFirst(Object o);
	void removeAll(Object o);
	void remove(int i);
	int getLength();
	<T> T[] toArray(T[] a);
	int getInt(int i);
	String getString(int i);
	long getLong(int i);
	DBObject getObject(int i);
}
