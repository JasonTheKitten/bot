package everyos.discord.chat4d.event;

import discord4j.core.event.domain.Event;
import discord4j.gateway.GatewayClient;
import everyos.bot.chat4j.ChatClient;
import everyos.bot.chat4j.ChatConnection;
import everyos.bot.chat4j.ChatShard;
import everyos.bot.chat4j.event.ChatEvent;
import everyos.discord.chat4d.DiscordShard;

public class DiscordEvent implements ChatEvent {
	private ChatConnection connection;
	private Event event;

	public DiscordEvent(ChatConnection connection, Event event) {
		this.connection = connection;
		this.event = event;
	}
	
	@Override public ChatClient getClient() {
		return getConnection().getClient();
	}
	@Override public ChatConnection getConnection() {
		return connection;
	}

	@Override
	public ChatShard getShard() {
		GatewayClient client = event.getClient().getGatewayClient(event.getShardInfo().getIndex()).get();
		return new DiscordShard(connection, client);
	}
}
