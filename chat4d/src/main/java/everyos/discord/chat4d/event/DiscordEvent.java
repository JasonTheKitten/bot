package everyos.discord.chat4d.event;

import everyos.bot.chat4j.ChatClient;
import everyos.bot.chat4j.ChatConnection;
import everyos.bot.chat4j.event.ChatEvent;

public class DiscordEvent implements ChatEvent {
	private ChatConnection connection;

	public DiscordEvent(ChatConnection connection) {
		this.connection = connection;
	}
	
	@Override public ChatClient getClient() {
		return getConnection().getClient();
	}
	@Override public ChatConnection getConnection() {
		return connection;
	}
}
