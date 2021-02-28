package everyos.bot.luwu.mongo.database;

import java.util.ArrayList;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.DoubleSupplier;
import java.util.function.IntSupplier;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

import javax.annotation.Nonnull;

import org.bson.Document;

import everyos.bot.luwu.core.database.DBArray;
import everyos.bot.luwu.core.database.DBObject;

public class MongoDBObject implements DBObject {
	Document document;

	public MongoDBObject(Document document) {
		this.document = document;
	}
	public MongoDBObject() {
		this.document = new Document();
	}

	@Override public String getOrDefaultString(@Nonnull String name, String def) {
    	if (document.containsKey(name)) return document.getString(name);
    	return def;
    }
	@Override public String getOrSetString(@Nonnull String name, String def) {
    	if (!document.containsKey(name)) document.put(name, def);
    	return document.getString(name);
    }
	@Override public String getOrCreateString(@Nonnull String name, Supplier<String> def) {
    	if (!document.containsKey(name)) document.put(name, def.get());
    	return document.getString(name);
    }
	
	@Override public int getOrDefaultInt(@Nonnull String name, int def) {
    	if (document.containsKey(name)) return document.getInteger(name);
    	return def;
    }
	@Override public int getOrSetInt(@Nonnull String name, int def) {
    	if (!document.containsKey(name)) document.put(name, def);
    	return document.getInteger(name);
    }
	@Override public int getOrCreateInt(@Nonnull String name, IntSupplier def) {
    	if (!document.containsKey(name)) document.put(name, def.getAsInt());
    	return document.getInteger(name);
    }
	
	@Override public long getOrDefaultLong(@Nonnull String name, long def) {
    	if (document.containsKey(name)) return document.getLong(name);
    	return def;
    }
	@Override public long getOrSetLong(@Nonnull String name, long def) {
    	if (!document.containsKey(name)) document.put(name, def);
    	return document.getLong(name);
    }
	@Override public long getOrCreateLong(@Nonnull String name, LongSupplier def) {
    	if (!document.containsKey(name)) document.put(name, def.getAsLong());
    	return document.getLong(name);
    }
	
	@Override public double getOrDefaultDouble(@Nonnull String name, long def) {
    	if (document.containsKey(name)) return document.getDouble(name);
    	return def;
    }
	@Override public double getOrSetDouble(@Nonnull String name, long def) {
    	if (!document.containsKey(name)) document.put(name, def);
    	return document.getDouble(name);
    }
	@Override public double getOrCreateDouble(@Nonnull String name, DoubleSupplier def) {
    	if (!document.containsKey(name)) document.put(name, def.getAsDouble());
    	return document.getDouble(name);
    }
	
	@Override public boolean getOrDefaultBoolean(@Nonnull String name, boolean def) {
    	if (document.containsKey(name)) return document.getBoolean(name);
    	return def;
    }
	@Override public boolean getOrSetBoolean(@Nonnull String name, boolean def) {
    	if (!document.containsKey(name)) document.put(name, def);
    	return document.getBoolean(name);
    }
	@Override public boolean getOrCreateBoolean(@Nonnull String name, BooleanSupplier def) {
    	if (!document.containsKey(name)) document.put(name, def.getAsBoolean());
    	return document.getBoolean(name);
    }
	
	@Override public DBObject getOrDefaultObject(@Nonnull String name, DBObject def) {
		if (document.containsKey(name)) return new MongoDBObject(document.get(name, Document.class));
    	return def;
    }
	@Override public MongoDBObject getOrCreateObject(@Nonnull String name, Consumer<DBObject> def) {
		MongoDBObject object = new MongoDBObject();
    	if (!document.containsKey(name)) {
    		document.put(name, object.document);
    	}
    	return new MongoDBObject(document.get(name, Document.class));
    }
	
	@SuppressWarnings("unchecked")
	@Override public DBArray getOrDefaultArray(@Nonnull String name, DBArray def) {
    	if (document.containsKey(name)) return new MongoDBArray(document.get(name, ArrayList.class));
    	return def;
    }
	@SuppressWarnings("unchecked")
	@Override public DBArray getOrCreateArray(@Nonnull String name) {
		MongoDBArray array = new MongoDBArray();
    	if (!document.containsKey(name)) {
    		document.put(name, array.data);
    	}
    	return new MongoDBArray(document.get(name, ArrayList.class));
    }

	@Override public void set(String key, Number i) {
		document.put(key, i);
	}
	@Override public void set(String key, String str) {
		document.put(key, str);
	}
	@Override public void set(String key, boolean bool) {
		document.put(key, bool);
    }
	@Override public void set(String k, Object v) {
		document.put(k, v);
	}
	
	@Override public void remove(String key) {
		document.remove(key);
	}
	
	@Override public MongoDBObject createObject(String key, Consumer<DBObject> func) {
		MongoDBObject obj = new MongoDBObject();
		func.accept(obj);
		document.put(key, obj.document);
		return obj;
	}
	@Override public MongoDBArray createArray(String key, Consumer<DBArray> func) {
		MongoDBArray arr = new MongoDBArray();
		func.accept(arr);
		document.put(key, arr.data);
		return arr;
	}
	
	@Override public boolean has(String key) {
		return document.containsKey(key);
	}
	
	@Override
	public String[] getKeys() {
		return document.keySet().toArray(new String[document.keySet().size()]);
	}
}
