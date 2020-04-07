package everyos.discord.bot.adapter;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.GuildChannel;
import discord4j.core.object.entity.MessageChannel;
import everyos.discord.bot.ShardInstance;
import everyos.storage.database.DBDocument;
import everyos.storage.database.DBObject;

public class GuildAdapter implements IAdapter {
    protected ShardInstance instance;
    protected String id;

    public GuildAdapter(ShardInstance instance, String id) {
        this.instance = instance;
        this.id = id;
    }

    public static GuildAdapter of(ShardInstance shard, MessageChannel channel) {
        return new GuildAdapter(shard, ((GuildChannel) channel).getGuildId().asString());
    }
    
    public static GuildAdapter of(ShardInstance shard, Guild guild) {
        return new GuildAdapter(shard, guild.getId().asString());
    }
    
    public static GuildAdapter of(ShardInstance shard, String id) {
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