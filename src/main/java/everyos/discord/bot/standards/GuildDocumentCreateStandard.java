package everyos.discord.bot.standards;

import java.util.function.Consumer;

import discord4j.core.object.entity.Guild;
import everyos.discord.bot.adapter.GuildAdapter;
import everyos.storage.database.DBDocument;

public class GuildDocumentCreateStandard implements Consumer<DBDocument> {
    public static GuildDocumentCreateStandard standard;

    static {
        standard = new GuildDocumentCreateStandard();
    }

    private Guild guild;

    public GuildDocumentCreateStandard() {}
    public GuildDocumentCreateStandard(Guild guild) {
        this.guild = guild;
    }

    @Override public void accept(DBDocument doc) {
        if (guild==null) {
            doc.putMemory("adapter", new GuildAdapter(doc.getName()));
        } else {
            doc.putMemory("adapter", new GuildAdapter(guild));
        }
    }
}