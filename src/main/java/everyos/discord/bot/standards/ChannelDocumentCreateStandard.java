package everyos.discord.bot.standards;

import java.util.function.Consumer;

import discord4j.core.object.entity.MessageChannel;
import everyos.discord.bot.adapter.ChannelAdapter;
import everyos.storage.database.DBDocument;

public class ChannelDocumentCreateStandard implements Consumer<DBDocument> {
    public static ChannelDocumentCreateStandard standard;

    static {
        standard = new ChannelDocumentCreateStandard();
    }

    private MessageChannel channel;

    public ChannelDocumentCreateStandard() {}
    public ChannelDocumentCreateStandard(MessageChannel channel) {
        this.channel = channel;
    }

    @Override public void accept(DBDocument doc) {
        if (channel==null) {
            doc.putMemory("adapter", new ChannelAdapter(doc.getName()));
        } else {
            doc.putMemory("adapter", new ChannelAdapter(channel));
        }
    }
}