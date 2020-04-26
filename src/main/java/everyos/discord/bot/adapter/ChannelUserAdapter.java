package everyos.discord.bot.adapter;


import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.Channel;
import everyos.discord.bot.BotInstance;
import everyos.discord.bot.database.DBDocument;
import reactor.core.publisher.Mono;

public class ChannelUserAdapter implements IAdapter {
	private BotInstance instance;
	private long channelID;
	private long userID;

	private ChannelUserAdapter(BotInstance instance, long channelID, long userID) {
		this.instance = instance;
		this.channelID = channelID;
		this.userID = userID;
	}
	
	public static ChannelUserAdapter of(BotInstance instance, Channel channel, long userID) {
		return new ChannelUserAdapter(instance, channel.getId().asLong(), userID);
	}
	public static ChannelUserAdapter of(BotInstance instance, Channel channel, User user) {
		return new ChannelUserAdapter(instance, channel.getId().asLong(), user.getId().asLong());
	}
	public static ChannelUserAdapter of(BotInstance instance, long channelID, User user) {
		return new ChannelUserAdapter(instance, channelID, user.getId().asLong());
	}

	@Override public Mono<DBDocument> getDocument() {
		return instance.db.collection("members").scan().with("uid", userID).with("cid", channelID).orSet(doc->{});
	}
}
