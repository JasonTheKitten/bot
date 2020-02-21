package everyos.discord.bot.standards;

import java.util.function.Consumer;

import everyos.storage.database.DBDocument;

public class GuildDocumentCreateStandard implements Consumer<DBDocument> {
    public static GuildDocumentCreateStandard standard;

    static {
        standard = new GuildDocumentCreateStandard();
    }

    public GuildDocumentCreateStandard() {}

    @Override public void accept(DBDocument doc) {}
}