package everyos.storage.database;

import java.io.File;
import java.util.HashMap;

import javax.annotation.Nonnull;

import everyos.discord.bot.util.FileUtil;

public class Database {
	private String path;
	private HashMap<String, DBCollection> cache;
    public Database(@Nonnull String dir) {
        this.path = dir;
        this.cache = new HashMap<String, DBCollection>();
        File mfile = new File(path);
		if (!mfile.exists()) mfile.mkdirs();
    }

    public DBCollection collection(@Nonnull String collection) {
    	if (!cache.containsKey(collection)) cache.put(collection, new DBCollection(FileUtil.join(path, collection)));
        return cache.get(collection);
    }
    
    public void delete(@Nonnull String collection) {
    	File collectionfi = new File(FileUtil.join(path, collection));
    	if (collectionfi.exists()) collectionfi.delete();
    }
}