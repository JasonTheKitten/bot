package everyos.discord.bot.adapter;

import discord4j.core.object.entity.channel.GuildMessageChannel;
import everyos.discord.bot.ShardInstance;
import everyos.storage.database.DBDocument;

public class TimedReminderAdapter implements IAdapter {
	private ShardInstance shard;
	private long guildID;
	private long reminderID;

	public TimedReminderAdapter(ShardInstance shard, long guildID, long reminderID) {
		this.shard = shard;
		this.guildID = guildID;
		this.reminderID = reminderID;
	}
	
	public static String create(long mid, String content, GuildMessageChannel channel, int time) {
		return null;
	}
	
	@Override public DBDocument getDocument() {
		return shard.instance.db.collection("guilds").getOrSet(guildID, doc->{}).subcollection("reminders").getOrSet(reminderID, doc->{});
	}
}
