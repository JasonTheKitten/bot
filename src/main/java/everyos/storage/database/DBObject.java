package everyos.storage.database;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.DoubleSupplier;
import java.util.function.IntSupplier;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

import javax.annotation.Nonnull;

import com.google.gson.JsonObject;

import everyos.storage.database.functional.ShortSupplier;

public class DBObject {
	JsonObject json;
	protected DBObject(JsonObject json) {
		this.json = json;
	}
	public DBObject() {
		this.json = new JsonObject();
	}
	
	public String getOrDefaultString(@Nonnull String name, String def) {
    	if (json.has(name)) return json.get(name).getAsString();
    	return def;
    }
	public String getOrSetString(@Nonnull String name, String def) {
    	if (!json.has(name)) json.addProperty(name, def);
    	return json.get(name).getAsString();
    }
	public String getOrCreateString(@Nonnull String name, Supplier<String> def) {
    	if (!json.has(name)) json.addProperty(name, def.get());
    	return json.get(name).getAsString();
    }
	
	public int getOrDefaultInt(@Nonnull String name, int def) {
    	if (json.has(name)) return json.get(name).getAsInt();
    	return def;
    }
	public int getOrSetInt(@Nonnull String name, int def) {
    	if (!json.has(name)) json.addProperty(name, def);
    	return json.get(name).getAsInt();
    }
	public int getOrCreateInt(@Nonnull String name, IntSupplier def) {
    	if (!json.has(name)) json.addProperty(name, def.getAsInt());
    	return json.get(name).getAsInt();
    }
	
	public long getOrDefaultLong(@Nonnull String name, long def) {
    	if (json.has(name)) return json.get(name).getAsLong();
    	return def;
    }
	public long getOrSetLong(@Nonnull String name, long def) {
    	if (!json.has(name)) json.addProperty(name, def);
    	return json.get(name).getAsLong();
    }
	public long getOrCreateLong(@Nonnull String name, LongSupplier def) {
    	if (!json.has(name)) json.addProperty(name, def.getAsLong());
    	return json.get(name).getAsLong();
    }
	
	public double getOrDefaultDouble(@Nonnull String name, long def) {
    	if (json.has(name)) return json.get(name).getAsDouble();
    	return def;
    }
	public double getOrSetDouble(@Nonnull String name, long def) {
    	if (!json.has(name)) json.addProperty(name, def);
    	return json.get(name).getAsDouble();
    }
	public double getOrCreateDouble(@Nonnull String name, DoubleSupplier def) {
    	if (!json.has(name)) json.addProperty(name, def.getAsDouble());
    	return json.get(name).getAsDouble();
    }
	
	public short getOrDefaultShort(@Nonnull String name, short def) {
    	if (json.has(name)) return json.get(name).getAsShort();
    	return def;
    }
	public short getOrSetShort(@Nonnull String name, short def) {
    	if (!json.has(name)) json.addProperty(name, def);
    	return json.get(name).getAsShort();
    }
	public short getOrCreateShort(@Nonnull String name, ShortSupplier def) {
    	if (!json.has(name)) json.addProperty(name, def.getAsShort());
    	return json.get(name).getAsShort();
    }
	
	public boolean getOrDefaultBoolean(@Nonnull String name, boolean def) {
    	if (json.has(name)) return json.get(name).getAsBoolean();
    	return def;
    }
	public boolean getOrSetBoolean(@Nonnull String name, boolean def) {
    	if (!json.has(name)) json.addProperty(name, def);
    	return json.get(name).getAsBoolean();
    }
	public boolean getOrCreateBoolean(@Nonnull String name, BooleanSupplier def) {
    	if (!json.has(name)) json.addProperty(name, def.getAsBoolean());
    	return json.get(name).getAsBoolean();
    }
	
	public DBObject getOrDefaultObject(@Nonnull String name, DBObject def) {
    	if (json.has(name)) return new DBObject(json.get(name).getAsJsonObject());
    	return def;
    }
	public DBObject getOrSetObject(@Nonnull String name, DBObject def) {
    	if (!json.has(name)) json.add(name, def.json);
    	return new DBObject(json.get(name).getAsJsonObject());
    }
	public DBObject getOrCreateObject(@Nonnull String name, Supplier<DBObject> def) {
    	if (!json.has(name)) json.add(name, def.get().json);
    	return new DBObject(json.get(name).getAsJsonObject());
    }
	
	public DBArray getOrDefaultArray(@Nonnull String name, DBArray def) {
    	if (json.has(name)) return new DBArray(json.get(name).getAsJsonArray());
    	return def;
    }
	public DBArray getOrSetArray(@Nonnull String name, DBArray def) {
    	if (!json.has(name)) json.add(name, def.json);
    	return new DBArray(json.get(name).getAsJsonArray());
    }
	public DBArray getOrCreateArray(@Nonnull String name, Supplier<DBArray> def) {
    	if (!json.has(name)) json.add(name, def.get().json);
    	return new DBArray(json.get(name).getAsJsonArray());
    }

	public void set(String key, Number i) {
		json.addProperty(key, i);
	}
	public void set(String key, String str) {
		json.addProperty(key, str);
	}
	public void set(String key, boolean bool) {
		json.addProperty(key, bool);
	}
	
	public DBObject createObject(String key, Consumer<DBObject> func) {
		DBObject obj = new DBObject();
		func.accept(obj);
		json.add(key, obj.json);
		return obj;
	}
	public DBArray createArray(String key, Consumer<DBArray> func) {
		DBArray arr = new DBArray();
		func.accept(arr);
		json.add(key, arr.json);
		return arr;
	}
	
	public boolean has(String key) {
		return json.has(key);
	}
}
