package everyos.bot.chat4j.entity;

import java.util.Optional;

import everyos.bot.chat4j.ChatConnection;
import everyos.bot.chat4j.functionality.ChatInterfaceProvider;
import reactor.core.publisher.Mono;

public interface ChatMessage extends ChatInterfaceProvider {
	/**
	 * Get this message's content
	 * @return An optional containing the message's content
	 */
	public Optional<String> getContent();
	
	/**
	 * Creates a mono to delete this message
	 * @return A mono that, when subscribed to, deletes this message
	 */
	public Mono<Void> delete();
	
	/**
	 * Creates a mono which returns the channel that this message was sent in
	 * @return A mono that, when subscribed to, returns the channel that this message was sent in
	 */
	public Mono<ChatChannel> getChannel();
	
	/**
	 * Creates a mono which returns the user that this message was sent by
	 * @return A mono that, when subscribed to, returns the user that this message was sent by
	 */
	public Mono<ChatUser> getAuthor();
	
	/**
	 * Creates a mono which returns the member that this message was sent by
	 * @return A mono that, when subscribed to, returns the member that this message was sent by
	 */
	public Mono<ChatMember> getAuthorAsMember();
	
	/**
	 * Get this message's creation date
	 * @return A timestamp representing this message's creation date
	 */
	public long getTimestamp();

	public ChatConnection getConnection();

	//TODO: This just temporarily resides here.
	public Mono<Void> pin();
	
	public long getChannelID();

	public long getAuthorID();

	public long getID();
}
