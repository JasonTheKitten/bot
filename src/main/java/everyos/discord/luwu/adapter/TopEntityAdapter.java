package everyos.discord.luwu.adapter;

import discord4j.core.object.entity.channel.Channel;
import discord4j.core.object.entity.channel.GuildChannel;
import everyos.discord.luwu.BotInstance;
import everyos.discord.luwu.database.DBDocument;
import reactor.core.publisher.Mono;

public class TopEntityAdapter implements IAdapter {
    private BotInstance instance;
    private boolean isGuild;
    private long id;

    public TopEntityAdapter(BotInstance instance, boolean isGuild, long id) {
        this.instance = instance;
        this.isGuild = isGuild;
        this.id = id;
    }

    public static TopEntityAdapter of(BotInstance instance, Channel channel) {
        if (channel instanceof GuildChannel) {
            return new TopEntityAdapter(instance, true, ((GuildChannel) channel).getGuildId().asLong());
        } else {
            return new TopEntityAdapter(instance, false, channel.getId().asLong());
        }
    }

    public IAdapter getPrimaryAdapter() {
        return isGuild?new GuildAdapter(instance, id):new ChannelAdapter(instance, id);
    }
    
    public boolean isOfGuild() {return isGuild;}

    @Override public Mono<DBDocument> getDocument() {
        return null; //instance.db.collection(isGuild?"guilds":"channels").getOrSet(id, document->{});
    }
}