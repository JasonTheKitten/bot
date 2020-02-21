package everyos.discord.bot.standards;

import java.util.function.Consumer;

import everyos.storage.database.DBDocument;

public class ChannelDocumentCreateStandard implements Consumer<DBDocument> {
    public static ChannelDocumentCreateStandard standard;

    static {
        standard = new ChannelDocumentCreateStandard();
    }

    public ChannelDocumentCreateStandard() {}

    @Override public void accept(DBDocument doc) {
        
    }
}