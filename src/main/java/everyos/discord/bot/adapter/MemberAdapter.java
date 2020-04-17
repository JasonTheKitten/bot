package everyos.discord.bot.adapter;


import java.util.function.BiFunction;
import java.util.function.Function;

import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.User;
import discord4j.rest.util.Snowflake;
import everyos.discord.bot.ShardInstance;
import everyos.storage.database.DBDocument;
import everyos.storage.database.DBObject;
import reactor.core.publisher.Mono;

public class MemberAdapter implements IAdapter {
    private GuildAdapter adapter;
    private long id;

    public MemberAdapter(GuildAdapter adapter, long id) {
        this.adapter = adapter;
        this.id = id;
    }

    public static MemberAdapter of(GuildAdapter adapter, long id) {
        return new MemberAdapter(adapter, id);
    }
    public static MemberAdapter of(GuildAdapter gadapter, User user) {
		return of(gadapter, user.getId().asLong());
	}
    public static MemberAdapter of(ShardInstance shard, Guild guild, User user) {
		return of(GuildAdapter.of(shard, guild), user);
	}
    public static MemberAdapter of(ShardInstance shard, Guild guild, long user) {
		return of(GuildAdapter.of(shard, guild), user);
	}
    
    public Mono<Member> getMember() {
    	return adapter.instance.client.getMemberById(Snowflake.of(adapter.id), Snowflake.of(id));
    }
    public long getMemberID() {
		return id;
	}
    
    @Override public boolean equals(Object obj) {
    	if (!(obj instanceof MemberAdapter)) return false;
    	MemberAdapter adp = (MemberAdapter) obj;
    	return adp.id==id && adp.adapter.id==adapter.id;
    }

    @Override public DBDocument getDocument() {
        return adapter.getDocument().subcollection("members").getOrSet(id, doc -> {});
    }

    public <T> T getData(Function<DBObject, T> func) {
		return getDocument().getObject(func);
	}
    public <T> T getData(BiFunction<DBObject, DBDocument, T> func) {
		return getDocument().getObject(func);
	}
}