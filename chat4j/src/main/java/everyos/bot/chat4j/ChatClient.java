package everyos.bot.chat4j;

import java.util.function.Function;

import everyos.bot.chat4j.functionality.ChatInterfaceProvider;
import reactor.core.publisher.Mono;

/**
 * A class representing a text-chat type client
 */
public interface ChatClient extends ChatInterfaceProvider {
	/**
	 * Create a mono to login to the client and create a connection
	 * @param func An action to perform once a connection is established, which is passed the connection. The returned mono is executed when subscribed to.
	 * @return A mono that, when subscribed to, logs in and creates a connection
	 */
	Mono<Void> login(Function<ChatConnection, Mono<?>> func);
}
