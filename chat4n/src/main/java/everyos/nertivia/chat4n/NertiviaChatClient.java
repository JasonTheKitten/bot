package everyos.nertivia.chat4n;

import java.util.function.Function;

import everyos.bot.chat4j.ChatClient;
import everyos.bot.chat4j.ChatConnection;
import everyos.bot.chat4j.functionality.ChatInterface;
import everyos.nertivia.nertivia4j.NertiviaClient;
import everyos.nertivia.nertivia4j.NertiviaClientBuilder;
import reactor.core.publisher.Mono;

public class NertiviaChatClient implements ChatClient {
	private NertiviaClient client;

	public NertiviaChatClient(String token) {
		this.client = NertiviaClientBuilder.create(token).build();
	}

	@Override public Mono<Void> login(Function<ChatConnection, Mono<?>> func) {
		return client.connect().map(connection->new NertiviaChatConnection(this, client, connection)).flatMap(func).then();
	}

	@Override public <T extends ChatInterface> boolean supportsInterface(Class<T> cls) {
		return false;
	}

	@Override public <T extends ChatInterface> T getInterface(Class<T> cls) {
		return null;
	}
	
	@Override public ChatClient getClient() {
		return this;
	}
}
