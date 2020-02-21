package everyos.discord.bot.standards;

import java.util.function.Consumer;

import everyos.storage.database.DBDocument;

public class MemberDocumentCreateStandard implements Consumer<DBDocument> {
    public static Consumer<DBDocument> standard;

    static {
        standard = new MemberDocumentCreateStandard();
    }

    @Override public void accept(DBDocument doc) {}
}