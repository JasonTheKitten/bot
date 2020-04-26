package everyos.discord.bot.adapter;

import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.channel.GuildChannel;
import everyos.discord.bot.BotInstance;
import everyos.discord.bot.database.DBDocument;
import everyos.discord.bot.database.DBResult;
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
}