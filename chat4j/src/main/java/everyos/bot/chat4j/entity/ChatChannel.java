package everyos.bot.chat4j.entity;

import everyos.bot.chat4j.ChatConnection;
import everyos.bot.chat4j.functionality.ChatInterfaceProvider;
import reactor.core.publisher.Mono;

/**
 * Represents a channel on the chat client
 */
public interface ChatChannel extends ChatInterfaceProvider {
	/**
	 * Get an ID representing this channel
	 * @return An ID representing this channel
	 */
	long getID();

	ChatConnection getConnection();
	
	Mono<ChatGuild> getGuild();

	String getName();
	
	boolean isPrivate();

	Mono<ChatMessage> getMessageByID(long mid);

	boolean isUnsafe();

	Mono<Void> delete();
}
