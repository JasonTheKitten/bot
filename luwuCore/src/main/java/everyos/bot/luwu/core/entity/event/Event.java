package everyos.bot.luwu.core.entity.event;

import everyos.bot.luwu.core.entity.Client;
import everyos.bot.luwu.core.entity.Connection;

public class Event {
	private Connection connection;

	public Event(Connection connection) {
		this.connection = connection;
	}
	
	public Connection getConnection() {
		return connection;
	}
	
	public Client getClient() {
		return connection.getClient();
	}
}
