package everyos.discord.luwu.adapter;

import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.channel.GuildChannel;
import everyos.discord.luwu.BotInstance;
import everyos.discord.luwu.database.DBDocument;
import everyos.discord.luwu.database.DBResult;
import reactor.core.publisher.Mono;

public class GuildAdapter implements IAdapter {
    protected BotInstance instance;
    protected long id;

    public GuildAdapter(BotInstance instance, long id) {
        this.instance = instance;
        this.id = id;
    }

    public static GuildAdapter of(BotInstance bot, GuildChannel channel) {
        return new GuildAdapter(bot, channel.getGuildId().asLong());
    }
    
    public static GuildAdapter of(BotInstance bot, Guild guild) {
        return new GuildAdapter(bot, guild.getId().asLong());
    }
    
    public static GuildAdapter of(BotInstance bot, long id) {
    	return new GuildAdapter(bot, id);
	}

    @Override public Mono<DBDocument> getDocument() {
		return instance.db.collection("guilds").scan().with("gid", id).orSet(doc->{});
	}

	public DBResult members() {
		return instance.db.collection("members").scan().with("gid", id);
	}

	public Mono<? extends Object> wipe() {
		return instance.db.collection("members").scan().with("gid", id).deleteAll()
			.then(instance.db.collection("messages").scan().with("gid", id).deleteAll())
			.then(instance.db.collection("modmembers").scan().with("gid", id).deleteAll())
			.then(instance.db.collection("guilds").scan().with("gid", id).deleteAll());
			//TODO: Clear chatlink entries
	}
}