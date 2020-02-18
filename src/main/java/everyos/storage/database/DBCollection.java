package everyos.storage.database;

import java.io.File;
import java.util.HashMap;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.annotation.Nonnull;

import everyos.discord.bot.util.FileUtil;
import everyos.storage.database.functional.OtherCase;

public class DBCollection {
	private String path;
	private HashMap<String, DBDocument> cache; //TODO: This should be <String, DBDocumentCache> (With an timestamp so we can release it when it get's old)
	
	protected DBCollection(@Nonnull String path) { //TODO: Throw exception if is directory
		this.path = path;
        this.cache = new HashMap<String, DBDocument>();
		File mfile = new File(path);
		if (!mfile.exists()) mfile.mkdirs();
	}
	
	public boolean has(@Nonnull String document) {
		return cache.containsKey(document) || new File(FileUtil.join(path, document)).exists();
	}

	public DBDocument getOrNull(@Nonnull String document) {
		if (cache.containsKey(document)) return cache.get(document);
		
		File collectionfi = new File(FileUtil.join(path, document));
		DBDocument dbdocument = cache.get(document);
    	if (dbdocument==null&&collectionfi.exists()) {
    		dbdocument = new DBDocument(FileUtil.join(path, document));
    		cache.put(document, dbdocument);
    	};
    	
    	return dbdocument;
	}
	public DBDocument getOrSet(@Nonnull String document, @Nonnull Consumer<? super DBDocument> func) {
        DBDocument dbdocument = getOrNull(document);
        
        if (dbdocument == null) {
        	dbdocument = new DBDocument(FileUtil.join(path, document));
        	func.accept(dbdocument);
        	cache.put(document, dbdocument);
        }
        
        return dbdocument;
	}
	public OtherCase getIfPresent(@Nonnull String document, @Nonnull Function<? super DBDocument, Boolean> func) {
		DBDocument dbobj = getOrNull(document);
		OtherCase elsedo = new OtherCase();
		if (dbobj!=null) {
			elsedo.complete = func.apply(dbobj);
		}
		return elsedo;
	}
	public OtherCase getIfPresent(@Nonnull String document, @Nonnull Consumer<? super DBDocument> func) {
		DBDocument dbobj = getOrNull(document);
		OtherCase elsedo = new OtherCase();
		if (dbobj!=null) {
			func.accept(dbobj);
			elsedo.complete = true;
		}
		return elsedo;
	}
	public void ifNotPresent(@Nonnull String document, @Nonnull Consumer<? super DBDocument> func) {
		DBDocument dbobj = getOrNull(document);
		if (dbobj==null) func.accept(dbobj);
	}
	
	public void delete(@Nonnull String document) {
    	File collectionfi = new File(FileUtil.join(path, document));
    	if (collectionfi.exists()) collectionfi.delete();
    	cache.remove(document);
    }
}