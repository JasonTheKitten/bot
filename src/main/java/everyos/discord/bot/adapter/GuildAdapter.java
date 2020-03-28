package everyos.discord.bot.adapter;

import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.GuildChannel;
import discord4j.core.object.entity.MessageChannel;
import everyos.discord.bot.ShardInstance;
import everyos.storage.database.DBDocument;
import reactor.core.publisher.Mono;

public class GuildAdapter implements IAdapter {
    protected ShardInstance instance;
    protected String id;

    public GuildAdapter(ShardInstance instance, String id) {
        this.instance = instance;
        this.id = id;
    }

    public static Mono<GuildAdapter> of(ShardInstance shard, MessageChannel channel) { //TODO: No mono
        if (!(channel instanceof GuildChannel)) return Mono.empty();
        return Mono.just(new GuildAdapter(shard, ((GuildChannel) channel).getGuildId().asString()));
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
}