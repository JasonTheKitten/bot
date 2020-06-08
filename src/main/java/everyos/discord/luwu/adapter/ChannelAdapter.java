package everyos.discord.luwu.adapter;

import everyos.discord.luwu.BotInstance;
import everyos.discord.luwu.database.DBDocument;
import reactor.core.publisher.Mono;

public class ChannelAdapter implements IAdapter {
    private BotInstance instance;
    private long id;

    public ChannelAdapter(BotInstance instance, long id) {
        this.instance = instance;
        this.id = id;
    }

    public static ChannelAdapter of(BotInstance instance, long id) {
        return new ChannelAdapter(instance, id);
    }

    @Override public Mono<DBDocument> getDocument() {
        return instance.db.collection("channels").scan().with("cid", id).orSet(doc->{});
    }
}