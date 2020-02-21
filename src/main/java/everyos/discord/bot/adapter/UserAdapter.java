package everyos.discord.bot.adapter;

import everyos.discord.bot.Main;
import everyos.discord.bot.standards.UserDocumentCreateStandard;
import everyos.discord.bot.util.ObjectStore;
import everyos.storage.database.DBCollection;
import everyos.storage.database.DBDocument;

public class UserAdapter implements IAdapter {
    String uid;

    public UserAdapter(String uid) {
        this.uid = uid;
    }

    @Override
    public DBDocument getDocument() {
        return Main.db.collection("users").getOrSet(uid, UserDocumentCreateStandard.standard);
    }

    public static UserAdapter of(String uid) {
        ObjectStore rtn = new ObjectStore();
        DBCollection collection = Main.db.collection("users");
        collection.getIfPresent(uid, channelo -> {
            rtn.object = channelo.getMemoryOrNull("adapter");
            return rtn.object != null;
        }).elsedo(() -> {
            rtn.object = collection.getOrSet(uid, UserDocumentCreateStandard.standard)
                .getMemoryOrSet("adapter", ()->{return new UserAdapter(uid);});
        });

        return (UserAdapter) rtn.object;
    }
}