package everyos.storage.database;

import java.util.function.IntConsumer;

import javax.annotation.Nonnull;

import com.google.gson.JsonArray;
import com.google.gson.JsonPrimitive;

public class DBArray {
	protected JsonArray json;
	protected DBArray(JsonArray json) {
        this.json = json;
	}
	public DBArray() {
		this(new JsonArray());
	}
	
	public int getLength() {
		return json.size();
	}
    public void forEach(IntConsumer func) {
		for (int i=0; i<getLength(); i++) {
			func.accept(i);
		}
	}
	
	public String getString(@Nonnull int key) {
    	return json.get(key).getAsString();
    }
	public int getInt(@Nonnull int key) {
    	return json.get(key).getAsInt();
    }
	public long getLong(@Nonnull int key) {
    	return json.get(key).getAsLong();
    }
	public double getDouble(@Nonnull int key) {
    	return json.get(key).getAsDouble();	
    }
	public short getShort(@Nonnull int key) {
    	return json.get(key).getAsShort();
    }
	public boolean getBoolean(@Nonnull int key) {
    	return json.get(key).getAsBoolean();
    }
	public DBObject getObject(@Nonnull int key) {
    	return new DBObject(json.get(key).getAsJsonObject());
    }
	public DBArray getArray(@Nonnull int key) {
    	return new DBArray(json.get(key).getAsJsonArray());
    }
	
	public void add(String str) {
		json.add(str);
	}
	public void add(int i) {
		json.add(i);
	}
	public void add(long l) {
		json.add(l);
	}
	public void add(double d) {
		json.add(d);
	}
	public void add(Short s) {
		json.add(s);
	}
	public void add(boolean b) {
		json.add(b);
	}
	public void add(DBObject obj) {
		json.add(obj.json);
	}
	public void add(DBArray arr) {
		json.add(arr.json);
	}
	
	public void remove(int i) {
		json.remove(i);
	}
	public void removeFirst(String s) {
		json.remove(new JsonPrimitive(s));
	}
	public void removeFirst(Number n) {
		json.remove(new JsonPrimitive(n));
	}
	public void removeFirst(Boolean b) {
		json.remove(new JsonPrimitive(b));
	}
	
	public boolean contains(String str) {
		return json.contains(new JsonPrimitive(str));
	}
	public boolean contains(long l) {
		return json.contains(new JsonPrimitive(l));
	}
}