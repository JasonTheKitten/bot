package everyos.discord.bot.adapter;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.channel.GuildChannel;
import everyos.discord.bot.ShardInstance;
import everyos.storage.database.DBDocument;
import everyos.storage.database.DBObject;

public class GuildAdapter implements IAdapter {
    protected ShardInstance instance;
    protected long id;

    public GuildAdapter(ShardInstance instance, long id) {
        this.instance = instance;
        this.id = id;
    }

    public static GuildAdapter of(ShardInstance shard, GuildChannel channel) {
        return new GuildAdapter(shard, channel.getGuildId().asLong());
    }
    
    public static GuildAdapter of(ShardInstance shard, Guild guild) {
        return new GuildAdapter(shard, guild.getId().asLong());
    }
    
    public static GuildAdapter of(ShardInstance shard, long id) {
    	return new GuildAdapter(shard, id);
	}

    @Override public DBDocument getDocument() {
        return instance.db.collection("guilds").getOrSet(id, doc->{});
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