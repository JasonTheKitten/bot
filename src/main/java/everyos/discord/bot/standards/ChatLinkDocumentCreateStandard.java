package everyos.discord.bot.standards;

import java.util.function.Consumer;

import everyos.discord.bot.Main;
import everyos.discord.bot.adapter.ChatLinkAdapter;
import everyos.storage.database.DBDocument;

public class ChatLinkDocumentCreateStandard implements Consumer<DBDocument> {
    public static ChatLinkDocumentCreateStandard standard;

    static {
        standard = new ChatLinkDocumentCreateStandard();
    }

    @Override public void accept(DBDocument doc) {}
    
    public static void ifExists(String clID, Consumer<ChatLinkAdapter> func, Runnable orelse) {
    	Main.db.collection("chatlinks").getIfPresent(clID, cldoc->{
    		func.accept(ChatLinkAdapter.of(clID));
    	}).elsedo(()->{
    		orelse.run();
    	});
    }
}