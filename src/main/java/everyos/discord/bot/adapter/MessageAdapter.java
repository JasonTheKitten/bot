package everyos.discord.bot.adapter;


import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import discord4j.core.object.entity.Channel;
import discord4j.core.object.entity.Message;
import everyos.discord.bot.ShardInstance;
import everyos.storage.database.DBDocument;
import everyos.storage.database.DBObject;

public class MessageAdapter implements IAdapter {
    private ShardInstance instance;
    private String id;
    private String cid;

    public MessageAdapter(ShardInstance instance, String cid, String id) {
        this.instance = instance;
        this.id = id;
        this.cid = cid;
    }

    public static MessageAdapter of(ShardInstance instance, String cid, String id) {
        return new MessageAdapter(instance, cid, id);
    }
    public static MessageAdapter of(ShardInstance instance, Channel channel, String id) {
        return new MessageAdapter(instance, channel.getId().asString(), id);
    }
    public static MessageAdapter of(ShardInstance instance, Channel channel, Message m) {
        return new MessageAdapter(instance, channel.getId().asString(), m.getId().asString());
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