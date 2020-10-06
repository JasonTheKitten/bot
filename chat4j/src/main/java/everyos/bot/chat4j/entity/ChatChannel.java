package everyos.bot.chat4j.entity;

import everyos.bot.chat4j.ChatConnection;
import everyos.bot.chat4j.functionality.ChatInterfaceProvider;

/**
 * Represents a channel on the chat client
 */
public interface ChatChannel extends ChatInterfaceProvider {
	/**
	 * Get an ID representing this channel
	 * @return An ID representing this channel
	 */
	public long getID();

	public ChatConnection getConnection();
}
