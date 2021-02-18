package everyos.bot.chat4j;

import everyos.bot.chat4j.entity.ChatChannel;
import everyos.bot.chat4j.entity.ChatGuild;
import everyos.bot.chat4j.entity.ChatUser;
import everyos.bot.chat4j.event.ChatEvent;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Represents a multitude of connections to a given client
 */
public interface ChatConnection {
	/**
	 * End the connection upon subscribing
	 * @return The mono to be subscribed too
	 */
	public Mono<Void> logout();
	
	/**
	 * Get this connection's client
	 * @return The client that this connection is attached to
	 */
	public ChatClient getClient();
	
	public <T extends ChatEvent> boolean supportsEvent(Class<T> cls);
	public <T extends ChatEvent> Flux<T> generateEventListener(Class<T> cls);

	Mono<ChatUser> getUserByID(long id);
	public Mono<ChatChannel> getChannelByID(long id);
	public Mono<ChatGuild> getGuildByID(long long1);
}
