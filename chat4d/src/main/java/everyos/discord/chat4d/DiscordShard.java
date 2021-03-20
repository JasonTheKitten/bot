package everyos.discord.chat4d;

import discord4j.gateway.GatewayClient;
import everyos.bot.chat4j.ChatClient;
import everyos.bot.chat4j.ChatConnection;
import everyos.bot.chat4j.ChatShard;
import everyos.bot.chat4j.functionality.ChatInterface;

public class DiscordShard implements ChatShard {
	
	private ChatConnection connection;
	private GatewayClient shard;

	public DiscordShard(ChatConnection connection, GatewayClient client) {
		this.connection = connection;
		this.shard = client;
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
		return shard.getResponseTime().toMillis();
	}

	@Override
	public ChatConnection getConnection() {
		return connection;
	}

}
