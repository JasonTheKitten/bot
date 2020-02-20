package everyos.storage.database;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.function.Supplier;

import javax.annotation.Nonnull;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import everyos.discord.bot.util.FileUtil;

public class DBDocument {
    private String path;
    private String file;
    private String name;
    private JsonObject json;
    private DBObject dbobject;
    private HashMap<String, Object> memory;

    protected DBDocument(String name, String path) {
        this.path = path;
        this.file = path + ".json";
        this.name = name;

        File fo = new File(file);
        if (fo.exists()) {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fo)));
                this.json = JsonParser.parseReader(reader).getAsJsonObject();
            } catch (Exception e) {
                e.printStackTrace();
                this.json = new JsonObject();
            }
        } else {
            this.json = new JsonObject();
        }
        this.dbobject = new DBObject(json);
    }

    public DBCollection subcollection(@Nonnull String collection) {
        return new DBCollection(FileUtil.join(path, collection));
    }

    public DBObject getObject() {
        return dbobject;
    }

    public String getName() {
        return name;
    }

    public void save() { // TODO: Locks?
        try {
            File fi = new File(file);
            if (!(fi.exists())) {
                fi.getParentFile().mkdirs();
                fi.createNewFile();
            }

            BufferedOutputStream file = new BufferedOutputStream(new FileOutputStream(this.file));
            file.write(json.toString().getBytes());
            file.close();
        } catch (IOException e) {
            e.printStackTrace(); // TODO: On error
        }
    }

    public void putMemory(String key, Object value) {
        if (this.memory == null) {
            memory = new HashMap<String, Object>();
        }
        memory.put(key, value);
    }
    public Object getMemoryOrNull(String key) {
    	if (this.memory == null) return null;
        return memory.get(key);
    }
    public Object getMemoryOrSet(String key, Supplier<Object> func) {
        Object obj = getMemoryOrNull(key);
        if (obj == null) {
        	putMemory(key, func.get());
        	return getMemoryOrNull(key);
        }
        
        return obj;
    }
}
