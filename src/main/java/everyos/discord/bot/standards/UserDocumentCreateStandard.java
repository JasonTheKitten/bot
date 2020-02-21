package everyos.discord.bot.standards;

import java.util.function.Consumer;

import everyos.storage.database.DBDocument;

public class UserDocumentCreateStandard implements Consumer<DBDocument> {
    public static UserDocumentCreateStandard standard;

    static {
        standard = new UserDocumentCreateStandard();
    }

    @Override public void accept(DBDocument doc) {}
}