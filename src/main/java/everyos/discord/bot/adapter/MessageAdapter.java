package everyos.discord.bot.adapter;


import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.Channel;
import everyos.discord.bot.ShardInstance;
import everyos.storage.database.DBDocument;
import everyos.storage.database.DBObject;

public class MessageAdapter implements IAdapter {
    private ShardInstance instance;
    private long id;
    private long cid;

    public MessageAdapter(ShardInstance instance, long cid, long id) {
        this.instance = instance;
        this.id = id;
        this.cid = cid;
    }

    public static MessageAdapter of(ShardInstance instance, long cid, long id) {
        return new MessageAdapter(instance, cid, id);
    }
    public static MessageAdapter of(ShardInstance instance, Channel channel, long id) {
        return new MessageAdapter(instance, channel.getId().asLong(), id);
    }
    public static MessageAdapter of(ShardInstance instance, Channel channel, Message m) {
        return new MessageAdapter(instance, channel.getId().asLong(), m.getId().asLong());
    }

    @Override public DBDocument getDocument() {
        return instance.db.collection("channels").getOrSet(cid, doc->{}).subcollection("messages").getOrSet(id, doc->{});
    }

	public <T> T getData(Function<DBObject, T> func) {
		return getDocument().getObject(func);
	}
    public <T> T getData(BiFunction<DBObject, DBDocument, T> func) {
		return getDocument().getObject(func);
	}
    public void getData(Consumer<DBObject> func) {
		getDocument().getObject(func);
	}
    public void getData(BiConsumer<DBObject, DBDocument> func) {
		getDocument().getObject(func);
	}
}