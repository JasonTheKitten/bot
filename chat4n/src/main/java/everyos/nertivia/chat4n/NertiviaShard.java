package everyos.nertivia.chat4n;

import everyos.bot.chat4j.ChatClient;
import everyos.bot.chat4j.ChatConnection;
import everyos.bot.chat4j.ChatShard;
import everyos.bot.chat4j.functionality.ChatInterface;

public class NertiviaShard implements ChatShard {
	private ChatConnection connection;

	public NertiviaShard(ChatConnection connection) {
		this.connection = connection;
	}

	@Override
	public <T extends ChatInterface> boolean supportsInterface(Class<T> cls) {
		return false;
	}

	@Override
	public <T extends ChatInterface> T getInterface(Class<T> cls) {
		return null;
	}

	@Override
	public ChatClient getClient() {
		return connection.getClient();
	}

	@Override
	public long getPing() {
		return -1;
	}

	@Override
	public ChatConnection getConnection() {
		return connection;
	}
}
