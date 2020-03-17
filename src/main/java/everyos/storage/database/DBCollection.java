package everyos.storage.database;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.annotation.Nonnull;

public class DBCollection {
    private String path;
    private HashMap<String, DBDocument> cache; // TODO: Store an expiration in DBDocument

    protected DBCollection(@Nonnull String path) { // TODO: Throw exception if is directory
        this.path = path;
        this.cache = new HashMap<String, DBDocument>();
        File mfile = new File(path);
        if (!mfile.exists())
            mfile.mkdirs();
    }

    public boolean has(@Nonnull String document) {
        return cache.containsKey(document) || new File(FileUtil.join(path, document+".json")).exists();
    }

    public DBDocument getOrNull(@Nonnull String document) {
        if (cache.containsKey(document))
            return cache.get(document);

        File collectionfi = new File(FileUtil.join(path, document + ".json"));
        DBDocument dbdocument = cache.get(document);
        if (dbdocument == null && collectionfi.exists()) { // TODO: Should be .json?
            dbdocument = new DBDocument(document, FileUtil.join(path, document));
            cache.put(document, dbdocument);
        }

        return dbdocument;
    }

    public DBDocument getOrSet(@Nonnull String document, @Nonnull Consumer<DBDocument> func) {
        DBDocument dbdocument = getOrNull(document);

        if (dbdocument == null) {
            dbdocument = new DBDocument(document, FileUtil.join(path, document));
            func.accept(dbdocument);
            cache.put(document, dbdocument);
        }

        return dbdocument;
    }

    public OtherCase getIfPresent(@Nonnull String document, @Nonnull Function<DBDocument, Boolean> func) {
        DBDocument dbobj = getOrNull(document);
        OtherCase elsedo = new OtherCase();
        if (dbobj != null) {
            elsedo.complete = func.apply(dbobj);
        }
        return elsedo;
    }

    public OtherCase getIfPresent(@Nonnull String document, @Nonnull Consumer<DBDocument> func) {
        DBDocument dbobj = getOrNull(document);
        OtherCase elsedo = new OtherCase();
        if (dbobj != null) {
            func.accept(dbobj);
            elsedo.complete = true;
        }
        return elsedo;
    }

    public void ifNotPresent(@Nonnull String document, @Nonnull Consumer<DBDocument> func) {
        DBDocument dbobj = getOrNull(document);
        if (dbobj == null)
            func.accept(dbobj);
    }

    public void delete(@Nonnull String document) {
        File collectionfi = new File(FileUtil.join(path, document));
        if (collectionfi.exists())
            collectionfi.delete();
        cache.remove(document);
    }

    public DBDocument[] query(Function<DBDocument, Boolean> func) {
        ArrayList<DBDocument> matches = new ArrayList<DBDocument>();
        File f = new File(path);
        for (String n : f.list()) {
            if (n.endsWith(".json")) {
                DBDocument doc = getOrNull(n.substring(0, n.length() - 5));
                if (doc != null && func.apply(doc))
                    matches.add(doc);
            }
        }
        return matches.toArray(new DBDocument[matches.size()]);
    }

    public DBDocument[] all() {
        return query(doc->true);
    }
}