package everyos.discord.bot.database;

import java.util.ArrayList;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.DoubleSupplier;
import java.util.function.IntSupplier;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

import javax.annotation.Nonnull;

import org.bson.Document;

public class DBObject {
	private Document document;

	public DBObject(Document document) {
		this.document = document;
	}
	public DBObject() {
		this.document = new Document();
	}

	public String getOrDefaultString(@Nonnull String name, String def) {
    	if (document.containsKey(name)) return document.getString(name);
    	return def;
    }
	public String getOrSetString(@Nonnull String name, String def) {
    	if (!document.containsKey(name)) document.put(name, def);
    	return document.getString(name);
    }
	public String getOrCreateString(@Nonnull String name, Supplier<String> def) {
    	if (!document.containsKey(name)) document.put(name, def.get());
    	return document.getString(name);
    }
	
	public int getOrDefaultInt(@Nonnull String name, int def) {
    	if (document.containsKey(name)) return document.getInteger(name);
    	return def;
    }
	public int getOrSetInt(@Nonnull String name, int def) {
    	if (!document.containsKey(name)) document.put(name, def);
    	return document.getInteger(name);
    }
	public int getOrCreateInt(@Nonnull String name, IntSupplier def) {
    	if (!document.containsKey(name)) document.put(name, def.getAsInt());
    	return document.getInteger(name);
    }
	
	public long getOrDefaultLong(@Nonnull String name, long def) {
    	if (document.containsKey(name)) return document.getLong(name);
    	return def;
    }
	public long getOrSetLong(@Nonnull String name, long def) {
    	if (!document.containsKey(name)) document.put(name, def);
    	return document.getLong(name);
    }
	public long getOrCreateLong(@Nonnull String name, LongSupplier def) {
    	if (!document.containsKey(name)) document.put(name, def.getAsLong());
    	return document.getLong(name);
    }
	
	public double getOrDefaultDouble(@Nonnull String name, long def) {
    	if (document.containsKey(name)) return document.getDouble(name);
    	return def;
    }
	public double getOrSetDouble(@Nonnull String name, long def) {
    	if (!document.containsKey(name)) document.put(name, def);
    	return document.getDouble(name);
    }
	public double getOrCreateDouble(@Nonnull String name, DoubleSupplier def) {
    	if (!document.containsKey(name)) document.put(name, def.getAsDouble());
    	return document.getDouble(name);
    }
	
	public boolean getOrDefaultBoolean(@Nonnull String name, boolean def) {
    	if (document.containsKey(name)) return document.getBoolean(name);
    	return def;
    }
	public boolean getOrSetBoolean(@Nonnull String name, boolean def) {
    	if (!document.containsKey(name)) document.put(name, def);
    	return document.getBoolean(name);
    }
	public boolean getOrCreateBoolean(@Nonnull String name, BooleanSupplier def) {
    	if (!document.containsKey(name)) document.put(name, def.getAsBoolean());
    	return document.getBoolean(name);
    }
	
	public DBObject getOrDefaultObject(@Nonnull String name, DBObject def) {
    	if (document.containsKey(name)) return new DBObject(document.get(name, Document.class));
    	return def;
    }
	public DBObject getOrSetObject(@Nonnull String name, DBObject def) {
    	if (!document.containsKey(name)) document.put(name, def.document);
    	return new DBObject(document.get(name, Document.class));
    }
	public DBObject getOrCreateObject(@Nonnull String name, Supplier<DBObject> def) {
    	if (!document.containsKey(name)) document.put(name, def.get().document);
    	return new DBObject(document.get(name, Document.class));
    }
	
	@SuppressWarnings("unchecked")
	public DBArray getOrDefaultArray(@Nonnull String name, DBArray def) {
    	if (document.containsKey(name)) return new DBArray(document.get(name, ArrayList.class));
    	return def;
    }
	@SuppressWarnings("unchecked")
	public DBArray getOrSetArray(@Nonnull String name, DBArray def) {
    	if (!document.containsKey(name)) document.put(name, def.data);
    	return new DBArray(document.get(name, ArrayList.class));
    }
	@SuppressWarnings("unchecked")
	public DBArray getOrCreateArray(@Nonnull String name, Supplier<DBArray> def) {
    	if (!document.containsKey(name)) document.put(name, def.get().data);
    	return new DBArray(document.get(name, ArrayList.class));
    }

	public void set(String key, Number i) {
		document.put(key, i);
	}
	public void set(String key, String str) {
		document.put(key, str);
	}
	public void set(String key, boolean bool) {
		document.put(key, bool);
    }
	public void set(String key, DBObject obj) {
		document.put(key, obj.document);
	}
	public void set(String k, Object v) {
		document.put(k, v);
	}
	
    public void remove(String key) {
		document.remove(key);
	}
	
	public DBObject createObject(String key, Consumer<DBObject> func) {
		DBObject obj = new DBObject();
		func.accept(obj);
		document.put(key, obj.document);
		return obj;
	}
	public DBArray createArray(String key, Consumer<DBArray> func) {
		DBArray arr = new DBArray();
		func.accept(arr);
		document.put(key, arr.data);
		return arr;
	}
	
	public boolean has(String key) {
		return document.containsKey(key);
	}
}
