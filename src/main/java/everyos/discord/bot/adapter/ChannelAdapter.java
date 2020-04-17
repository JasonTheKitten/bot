package everyos.discord.bot.adapter;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

import everyos.discord.bot.ShardInstance;
import everyos.storage.database.DBDocument;
import everyos.storage.database.DBObject;

public class ChannelAdapter implements IAdapter {
    private ShardInstance instance;
    private long id;

    public ChannelAdapter(ShardInstance instance, long id) {
        this.instance = instance;
        this.id = id;
    }

    public static ChannelAdapter of(ShardInstance instance, long id) {
        return new ChannelAdapter(instance, id);
    }

    @Override public DBDocument getDocument() {
        return instance.db.collection("channels").getOrSet(id, doc->{});
    }

	public <T> T getData(Function<DBObject, T> func) {
		return getDocument().getObject(func);
	}
    public <T> T getData(BiFunction<DBObject, DBDocument, T> func) {
		return getDocument().getObject(func);
	}
    public void getData(BiConsumer<DBObject, DBDocument> func) {
		getDocument().getObject(func);
	}
}