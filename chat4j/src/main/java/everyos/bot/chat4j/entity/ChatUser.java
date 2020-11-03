package everyos.bot.chat4j.entity;

import everyos.bot.chat4j.ChatClient;
import everyos.bot.chat4j.ChatConnection;
import everyos.bot.chat4j.functionality.ChatInterfaceProvider;
import reactor.core.publisher.Mono;

public interface ChatUser extends ChatInterfaceProvider {
	/**
	 * Converts this user into a channel member
	 * @param channel The channel that the resulting member will be associated with
	 * @return The new member entity
	 */
	Mono<ChatMember> asMemberOf(ChatChannel channel);

	/**
	 * Get an ID representing this user
	 * @return An ID to represent this user
	 */
	long getID();

	ChatConnection getConnection();
	ChatClient getClient();

	String getHumanReadableID();

	Mono<ChatChannel> getPrivateChannel();

	boolean isBot();
}
