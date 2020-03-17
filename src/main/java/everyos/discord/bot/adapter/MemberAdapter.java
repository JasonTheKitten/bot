package everyos.discord.bot.adapter;


import discord4j.core.object.entity.Member;
import discord4j.core.object.util.Snowflake;
import everyos.storage.database.DBDocument;
import reactor.core.publisher.Mono;

public class MemberAdapter implements IAdapter {
    private GuildAdapter adapter;
    private String id;

    public MemberAdapter(GuildAdapter adapter, String id) {
        this.adapter = adapter;
        this.id = id;
    }

    public static MemberAdapter of(GuildAdapter adapter, String id) {
        return new MemberAdapter(adapter, id);
    }
    
    public Mono<Member> getMember() {
    	return adapter.instance.client.getMemberById(Snowflake.of(adapter.id), Snowflake.of(id));
    }
    
    @Override public boolean equals(Object obj) {
    	if (!(obj instanceof MemberAdapter)) return false;
    	MemberAdapter adp = (MemberAdapter) obj;
    	return adp.id.equals(id) && adp.adapter.id.equals(adapter.id);
    }

    @Override public DBDocument getDocument() {
        return adapter.getDocument().subcollection("members").getOrSet(id, doc -> {});
    }
}