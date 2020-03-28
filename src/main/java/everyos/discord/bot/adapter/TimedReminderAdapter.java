package everyos.discord.bot.adapter;

import discord4j.core.object.entity.GuildMessageChannel;
import everyos.discord.bot.ShardInstance;
import everyos.storage.database.DBDocument;

public class TimedReminderAdapter implements IAdapter {
	private ShardInstance shard;
	private String guildID;
	private String reminderID;

	public TimedReminderAdapter(ShardInstance shard, String guildID, String reminderID) {
		this.shard = shard;
		this.guildID = guildID;
		this.reminderID = reminderID;
	}
	
	public static String create(String mid, String content, GuildMessageChannel channel, int time) {
		return null;
	}
	
	@Override public DBDocument getDocument() {
		return shard.instance.db.collection("guilds").getOrSet(guildID, doc->{}).subcollection("reminders").getOrSet(reminderID, doc->{});
	}
}
