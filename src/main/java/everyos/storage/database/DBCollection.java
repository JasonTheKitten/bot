package everyos.storage.database;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.annotation.Nonnull;

public class DBCollection {
    private String path;
    private HashMap<Long, DBDocument> cache; // TODO: Store an expiration in DBDocument

    protected DBCollection(@Nonnull String path) { // TODO: Throw exception if is directory
        this.path = path;
        this.cache = new HashMap<Long, DBDocument>();
        File mfile = new File(path);
        if (!mfile.exists())
            mfile.mkdirs();
    }

    public boolean has(long document) {
        return cache.containsKey(document) || new File(FileUtil.join(path, document+".json")).exists();
    }

    public DBDocument getOrNull(long document) {
        if (cache.containsKey(document))
            return cache.get(document);

        File collectionfi = new File(FileUtil.join(path, document + ".json"));
        DBDocument dbdocument = cache.get(document);
        if (dbdocument == null && collectionfi.exists()) { // TODO: Should be .json?
            dbdocument = new DBDocument(document, FileUtil.join(path, String.valueOf(document)));
            cache.put(document, dbdocument);
        }

        return dbdocument;
    }

    public DBDocument getOrSet(long document, @Nonnull Consumer<DBDocument> func) {
        DBDocument dbdocument = getOrNull(document);

        if (dbdocument == null) {
            dbdocument = new DBDocument(document, FileUtil.join(path, String.valueOf(document)));
            func.accept(dbdocument);
            cache.put(document, dbdocument);
        }

        return dbdocument;
    }

    public OtherCase getIfPresent(long document, @Nonnull Function<DBDocument, Boolean> func) {
        DBDocument dbobj = getOrNull(document);
        OtherCase elsedo = new OtherCase();
        if (dbobj != null) {
            elsedo.complete = func.apply(dbobj);
        }
        return elsedo;
    }

    public OtherCase getIfPresent(long document, @Nonnull Consumer<DBDocument> func) {
        DBDocument dbobj = getOrNull(document);
        OtherCase elsedo = new OtherCase();
        if (dbobj != null) {
            func.accept(dbobj);
            elsedo.complete = true;
        }
        return elsedo;
    }

    public void ifNotPresent(long document, @Nonnull Consumer<DBDocument> func) {
        DBDocument dbobj = getOrNull(document);
        if (dbobj == null)
            func.accept(dbobj);
    }

    public void delete(long document) {
        File collectionfi = new File(FileUtil.join(path, String.valueOf(document)));
        if (collectionfi.exists())
            collectionfi.delete();
        cache.remove(document);
    }

    public DBDocument[] query(Function<DBDocument, Boolean> func) {
        ArrayList<DBDocument> matches = new ArrayList<DBDocument>();
        File f = new File(path);
        for (String n : f.list()) {
            if (n.endsWith(".json")) {
            	try {
	                DBDocument doc = getOrNull(Long.valueOf(n.substring(0, n.length() - 5)));
	                if (doc != null && func.apply(doc))
	                    matches.add(doc);
            	} catch(Exception e) {
            		e.printStackTrace();
            	}
            }
        }
        return matches.toArray(new DBDocument[matches.size()]);
    }

    public DBDocument[] all() {
        return query(doc->true);
    }
}