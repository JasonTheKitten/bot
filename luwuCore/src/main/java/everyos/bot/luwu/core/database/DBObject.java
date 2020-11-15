package everyos.bot.luwu.core.database;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.DoubleSupplier;
import java.util.function.IntSupplier;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

public interface DBObject {
	//TODO: Is too hacky feeling
	//Databases should never be mixed, but eh...
	String getOrDefaultString(String name, String def);
	String getOrSetString(String name, String def);
	String getOrCreateString(String name, Supplier<String> def);
	int getOrDefaultInt(String name, int def);
	int getOrSetInt(String name, int def);
	int getOrCreateInt(String name, IntSupplier def);
	long getOrDefaultLong(String name, long def);
	long getOrSetLong(String name, long def);
	long getOrCreateLong(String name, LongSupplier def);
	double getOrDefaultDouble(String name, long def);
	double getOrSetDouble(String name, long def);
	double getOrCreateDouble(String name, DoubleSupplier def);
	boolean getOrDefaultBoolean(String name, boolean def);
	boolean getOrSetBoolean(String name, boolean def);
	boolean getOrCreateBoolean(String name, BooleanSupplier def);
	DBObject getOrDefaultObject(String name, DBObject def);
	DBObject getOrCreateObject(String name, Consumer<DBObject> def);
	DBArray getOrDefaultArray(String name, DBArray def);
	DBArray getOrCreateArray(String name);
	void set(String key, Number i);
	void set(String key, String str);
	void set(String key, boolean bool);
	void set(String k, Object v);
	void remove(String key);
	DBObject createObject(String key, Consumer<DBObject> func);
	DBArray createArray(String key, Consumer<DBArray> func);
	boolean has(String key);
}
