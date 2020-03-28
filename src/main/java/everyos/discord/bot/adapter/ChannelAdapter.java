package everyos.discord.bot.adapter;

import everyos.discord.bot.ShardInstance;
import everyos.storage.database.DBDocument;

public class ChannelAdapter implements IAdapter {
    private ShardInstance instance;
    private String id;

    public ChannelAdapter(ShardInstance instance, String id) {
        this.instance = instance;
        this.id = id;
    }

    public static ChannelAdapter of(ShardInstance instance, String id) {
        return new ChannelAdapter(instance, id);
    }

    @Override public DBDocument getDocument() {
        return instance.db.collection("channels").getOrSet(id, doc->{});
    }
}