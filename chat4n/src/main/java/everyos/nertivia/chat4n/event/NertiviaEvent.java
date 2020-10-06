package everyos.nertivia.chat4n.event;

import everyos.bot.chat4j.ChatClient;
import everyos.bot.chat4j.ChatConnection;
import everyos.bot.chat4j.event.ChatEvent;

public class NertiviaEvent implements ChatEvent {
	private ChatConnection connection;

	public NertiviaEvent(ChatConnection connection) {
		this.connection = connection;
	}
	
	@Override public ChatClient getClient() {
		return getConnection().getClient();
	}

	@Override public ChatConnection getConnection() {
		return connection;
	}
}
