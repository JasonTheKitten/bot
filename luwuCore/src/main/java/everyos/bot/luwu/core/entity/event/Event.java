package everyos.bot.luwu.core.entity.event;

import everyos.bot.chat4j.event.ChatEvent;
import everyos.bot.luwu.core.command.Shard;
import everyos.bot.luwu.core.entity.Client;
import everyos.bot.luwu.core.entity.Connection;

public class Event {
	private Connection connection;
	private ChatEvent event;

	public Event(Connection connection, ChatEvent event) {
		this.connection = connection;
		this.event = event;
	}
	
	public Shard getShard() {
		return new Shard(connection, event.getShard());
	}
	
	public Connection getConnection() {
		return connection;
	}
	
	public Client getClient() {
		return connection.getClient();
	}
}
