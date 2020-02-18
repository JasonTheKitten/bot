package everyos.storage.database;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.annotation.Nonnull;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import everyos.discord.bot.util.FileUtil;

public class DBDocument {
	private String path;
	private String file;
	private JsonObject json;
	private DBObject dbobject;
	
	protected DBDocument(String path) {
		this.path = path;
		this.file = path+".json";
		
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
	
	public void save() { //TODO: Locks?
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
			e.printStackTrace(); //TODO: On error
		}
	}
}
