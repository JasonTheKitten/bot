package everyos.discord.bot.adapter;

import discord4j.core.object.entity.channel.GuildMessageChannel;
import everyos.discord.bot.ShardInstance;
import everyos.storage.database.DBDocument;

public class PresenceReminderAdapter implements IAdapter {
	private ShardInstance shard;
	private long memberID;
	private long reminderID;

	public PresenceReminderAdapter(ShardInstance shard, long memberID, long reminderID) {
		this.shard = shard;
		this.memberID = memberID;
		this.reminderID = reminderID;
	}
	
	public static String create(long mid, String content, GuildMessageChannel channel) {
		return null;
	}
	
	@Override public DBDocument getDocument() {
		return shard.instance.db.collection("users").getOrSet(memberID, doc->{}).subcollection("reminders").getOrSet(reminderID, doc->{});
	}
}
