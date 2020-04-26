package everyos.discord.bot.adapter;


import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.User;
import discord4j.rest.util.Snowflake;
import everyos.discord.bot.BotInstance;
import everyos.discord.bot.database.DBDocument;
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
    public static MemberAdapter of(BotInstance bot, Guild guild, User user) {
		return of(GuildAdapter.of(bot, guild), user);
	}
    public static MemberAdapter of(BotInstance bot, Guild guild, long user) {
		return of(GuildAdapter.of(bot, guild), user);
	}
    
    public Mono<Member> getMember() {
    	return adapter.instance.client.getMemberById(Snowflake.of(adapter.id), Snowflake.of(id));
    }
    public ModMemberAdapter toMod() {
    	return ModMemberAdapter.of(adapter, id);
    }
    public long getUserID() {
		return id;
	}
    
    @Override public boolean equals(Object obj) {
    	if (!(obj instanceof MemberAdapter)) return false;
    	MemberAdapter adp = (MemberAdapter) obj;
    	return adp.id==id && adp.adapter.id==adapter.id;
    }

    @Override public Mono<DBDocument> getDocument() {
		return adapter.instance.db.collection("members").scan().with("uid", id).with("gid", adapter.id).orSet(doc->{});
	}
}