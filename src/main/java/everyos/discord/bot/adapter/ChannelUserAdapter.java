package everyos.discord.bot.adapter;


import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.Channel;
import everyos.discord.bot.ShardInstance;
import everyos.storage.database.DBDocument;
import everyos.storage.database.DBObject;

public class ChannelUserAdapter implements IAdapter {
	private ShardInstance instance;
	private long channelID;
	private long userID;

	private ChannelUserAdapter(ShardInstance instance, long channelID, long userID) {
		this.instance = instance;
		this.channelID = channelID;
		this.userID = userID;
	}
	
	public static ChannelUserAdapter of(ShardInstance instance, Channel channel, long userID) {
		return new ChannelUserAdapter(instance, channel.getId().asLong(), userID);
	}
	public static ChannelUserAdapter of(ShardInstance instance, Channel channel, User user) {
		return new ChannelUserAdapter(instance, channel.getId().asLong(), user.getId().asLong());
	}
	public static ChannelUserAdapter of(ShardInstance instance, long channelID, User user) {
		return new ChannelUserAdapter(instance, channelID, user.getId().asLong());
	}

	@Override public DBDocument getDocument() {
		return instance.db.collection("channels").getOrSet(channelID, doc->{}).subcollection("users").getOrSet(userID, doc->{});
	}

	public <T> T getData(Function<DBObject, T> func) {
		return getDocument().getObject(func);
	}
    public <T> T getData(BiFunction<DBObject, DBDocument, T> func) {
		return getDocument().getObject(func);
	}
    public void getData(Consumer<DBObject> func) {
		getDocument().getObject(func);
	}
    public void getData(BiConsumer<DBObject, DBDocument> func) {
		getDocument().getObject(func);
	}
}
