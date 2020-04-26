package everyos.discord.bot.adapter;

import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.User;
import everyos.discord.bot.BotInstance;
import everyos.discord.bot.database.DBDocument;
import reactor.core.publisher.Mono;

public class ModMemberAdapter implements IAdapter {
	private GuildAdapter adapter;
    private long id;

    public ModMemberAdapter(GuildAdapter adapter, long id) {
        this.adapter = adapter;
        this.id = id;
    }

    public static ModMemberAdapter of(GuildAdapter adapter, long id) {
        return new ModMemberAdapter(adapter, id);
    }
    public static ModMemberAdapter of(GuildAdapter gadapter, User user) {
		return of(gadapter, user.getId().asLong());
	}
    public static ModMemberAdapter of(BotInstance bot, Guild guild, User user) {
		return of(GuildAdapter.of(bot, guild), user);
	}
    public static ModMemberAdapter of(BotInstance bot, Guild guild, long user) {
		return of(GuildAdapter.of(bot, guild), user);
	}
	
	@Override public Mono<DBDocument> getDocument() {
		return adapter.instance.db.collection("modmembers").scan().with("uid", id).with("gid", adapter.id).orSet(doc->{});
	}
}
