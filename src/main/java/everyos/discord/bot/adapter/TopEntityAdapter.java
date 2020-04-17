package everyos.discord.bot.adapter;

import discord4j.core.object.entity.channel.Channel;
import discord4j.core.object.entity.channel.GuildChannel;
import everyos.discord.bot.ShardInstance;
import everyos.storage.database.DBDocument;

public class TopEntityAdapter implements IAdapter {
    private ShardInstance instance;
    private boolean isGuild;
    private long id;

    public TopEntityAdapter(ShardInstance instance, boolean isGuild, long id) {
        this.instance = instance;
        this.isGuild = isGuild;
        this.id = id;
    }

    public static TopEntityAdapter of(ShardInstance instance, Channel channel) {
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

    @Override public DBDocument getDocument() {
        return instance.db.collection(isGuild?"guilds":"channels").getOrSet(id, document->{});
    }
}