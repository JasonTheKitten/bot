package everyos.discord.bot.adapter;


import java.util.function.BiConsumer;
import java.util.function.Consumer;

import discord4j.core.object.entity.Channel;
import discord4j.core.object.entity.User;
import everyos.discord.bot.ShardInstance;
import everyos.storage.database.DBDocument;
import everyos.storage.database.DBObject;

public class ChannelUserAdapter implements IAdapter {
	private ShardInstance instance;
	private String channelID;
	private String userID;

	private ChannelUserAdapter(ShardInstance instance, String channelID, String userID) {
		this.instance = instance;
		this.channelID = channelID;
		this.userID = userID;
	}
	
	public static ChannelUserAdapter of(ShardInstance instance, Channel channel, String userID) {
		return new ChannelUserAdapter(instance, channel.getId().asString(), userID);
	}
	public static ChannelUserAdapter of(ShardInstance instance, Channel channel, User user) {
		return new ChannelUserAdapter(instance, channel.getId().asString(), user.getId().asString());
	}
	public static ChannelUserAdapter of(ShardInstance instance, String channelID, User user) {
		return new ChannelUserAdapter(instance, channelID, user.getId().asString());
	}

	@Override public DBDocument getDocument() {
		return instance.db.collection("channels").getOrSet(channelID, doc->{}).subcollection("users").getOrSet(userID, doc->{});
	}

	public void getData(Consumer<DBObject> func) {
		getDocument().getObject(func);
	}
    public void getData(BiConsumer<DBObject, DBDocument> func) {
		getDocument().getObject(func);
	}
}
