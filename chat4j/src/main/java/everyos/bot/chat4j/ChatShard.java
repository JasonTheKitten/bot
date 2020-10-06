package everyos.bot.chat4j;

import everyos.bot.chat4j.functionality.ChatInterfaceProvider;

/**
 * Represents one connection, out of a series of connections
 */
public interface ChatShard extends ChatInterfaceProvider {
	/**
	 * Get the client associated with this shard
	 * @return The client	 */
	public ChatClient getClient();
}
