package everyos.bot.luwu.core.command;

import everyos.bot.chat4j.ChatShard;
import everyos.bot.luwu.core.entity.Client;
import everyos.bot.luwu.core.entity.Connection;

public class Shard {
	private Connection connection;
	private ChatShard shard;

	public Shard(Connection connection, ChatShard shard) {
		this.connection = connection;
		this.shard = shard;
	}
	
	public long getPing() {
		return shard.getPing();
	}
	
	public Client getClient() {
		return connection.getClient();
	}
	
	public Connection getConnection() {
		return connection;
	}
}
