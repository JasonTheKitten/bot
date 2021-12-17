package everyos.discord.chat4d;

import java.util.function.Function;

import discord4j.core.DiscordClient;
import discord4j.core.shard.GatewayBootstrap;
import discord4j.gateway.GatewayOptions;
import discord4j.gateway.intent.Intent;
import discord4j.gateway.intent.IntentSet;
import everyos.bot.chat4j.ChatClient;
import everyos.bot.chat4j.ChatConnection;
import everyos.bot.chat4j.functionality.ChatInterface;
import reactor.core.publisher.Mono;

public class DiscordChatClient implements ChatClient {
	private DiscordClient client;

	public DiscordChatClient(String token) {
		this.client = DiscordClient.create(token);
	}
	
	@Override
	public Mono<Void> login(Function<ChatConnection, Mono<?>> func) {
		GatewayBootstrap<GatewayOptions> gb = client.gateway()
			.setEnabledIntents(IntentSet.all())
			.setDisabledIntents(IntentSet.of(Intent.GUILD_PRESENCES));
		return gb.withGateway(connection->func.apply(new DiscordConnection(this, connection)));
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
		return this;
	}
}
