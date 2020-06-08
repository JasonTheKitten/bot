package everyos.discord.luwu.adapter;


import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.Channel;
import everyos.discord.luwu.BotInstance;
import everyos.discord.luwu.database.DBDocument;
import reactor.core.publisher.Mono;

public class MessageAdapter implements IAdapter {
    private BotInstance instance;
    private long id;
    private long cid;

    public MessageAdapter(BotInstance instance, long cid, long id) {
        this.instance = instance;
        this.id = id;
        this.cid = cid;
    }

    public static MessageAdapter of(BotInstance instance, long cid, long id) {
        return new MessageAdapter(instance, cid, id);
    }
    public static MessageAdapter of(BotInstance instance, Channel channel, long id) {
        return new MessageAdapter(instance, channel.getId().asLong(), id);
    }
    public static MessageAdapter of(BotInstance instance, Channel channel, Message m) {
        return new MessageAdapter(instance, channel.getId().asLong(), m.getId().asLong());
    }

    @Override public Mono<DBDocument> getDocument() {
		return instance.db.collection("messages").scan().with("cid", cid).with("mid", id).orSet(doc->{});
	}
}