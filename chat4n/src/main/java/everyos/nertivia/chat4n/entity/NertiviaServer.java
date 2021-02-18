package everyos.nertivia.chat4n.entity;

import everyos.bot.chat4j.ChatClient;
import everyos.bot.chat4j.ChatConnection;
import everyos.bot.chat4j.entity.ChatGuild;
import everyos.bot.chat4j.functionality.ChatInterface;
import everyos.nertivia.nertivia4j.entity.Server;

public class NertiviaServer implements ChatGuild {
	private Server server;
	private ChatConnection connection;

	public NertiviaServer(ChatConnection connection, Server server) {
		this.connection = connection;
		this.server = server;
	}

	@Override public <T extends ChatInterface> boolean supportsInterface(Class<T> cls) {
		return false;
	}

	@Override public <T extends ChatInterface> T getInterface(Class<T> cls) {
		return null;
	}

	@Override public ChatClient getClient() {
		return connection.getClient();
	}

	@Override public long getID() {
		return server.getID();
	}

	@Override public String getName() {
		return "PLEASE IMPLEMENT";
	}

}
