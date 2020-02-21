package everyos.discord.bot.adapter;

import everyos.discord.bot.standards.MemberDocumentCreateStandard;
import everyos.discord.bot.util.ObjectStore;
import everyos.storage.database.DBCollection;
import everyos.storage.database.DBDocument;

public class MemberAdapter implements IAdapter {
    private IAdapter padapter;
    @SuppressWarnings("unused") private UserAdapter uadapter;
    private String uid;
    
    public MemberAdapter(IAdapter padapter, UserAdapter uadapter) {
        this.padapter = padapter;
        this.uadapter = uadapter;
        this.uid = uadapter.uid;
    }

    @Override public DBDocument getDocument() {
        return padapter.getDocument().subcollection("members").getOrSet(uid, MemberDocumentCreateStandard.standard);
    }

    public static MemberAdapter of(IAdapter padapter, UserAdapter uadapter) {
        ObjectStore rtn = new ObjectStore();
        DBCollection collection = padapter.getDocument().subcollection("members");
        collection.getIfPresent(uadapter.uid, channelo -> {
            rtn.object = channelo.getMemoryOrNull("adapter");
            return rtn.object != null;
        }).elsedo(() -> {
            rtn.object = collection.getOrSet(uadapter.uid, MemberDocumentCreateStandard.standard)
                .getMemoryOrSet("adapter", ()->{return new MemberAdapter(padapter, uadapter);});
        });

        return (MemberAdapter) rtn.object;
    }
}