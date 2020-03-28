package everyos.discord.bot.adapter;

import discord4j.core.object.entity.GuildMessageChannel;
import everyos.discord.bot.ShardInstance;
import everyos.storage.database.DBDocument;

public class PresenceReminderAdapter implements IAdapter {
	private ShardInstance shard;
	private String memberID;
	private String reminderID;

	public PresenceReminderAdapter(ShardInstance shard, String memberID, String reminderID) {
		this.shard = shard;
		this.memberID = memberID;
		this.reminderID = reminderID;
	}
	
	public static String create(String mid, String content, GuildMessageChannel channel) {
		return null;
	}
	
	@Override public DBDocument getDocument() {
		return shard.instance.db.collection("users").getOrSet(memberID, doc->{}).subcollection("reminders").getOrSet(reminderID, doc->{});
	}
}
