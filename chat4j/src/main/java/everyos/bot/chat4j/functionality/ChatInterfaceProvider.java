package everyos.bot.chat4j.functionality;

import everyos.bot.chat4j.ChatClient;

public interface ChatInterfaceProvider {
	/**
	 * Check if this entity supports the given functionality
	 * @param <T> The functionality to query support for
	 * @param cls The interface of the functionality to query support for
	 * @return A boolean indicating whether the functionality is supported
	 */
	public <T extends ChatInterface> boolean supportsInterface(Class<T> cls);
	
	/**
	 * Get an endpoint of the client
	 * @param <T> The functionality to get an instance of
	 * @param cls The interface of the functionality to get an instance of
	 * @return An instance of the specified fuctionality
	 * @throws UnsupportedInterfaceException Thrown if the functionality is not supported
	 */
	public <T extends ChatInterface> T getInterface(Class<T> cls);
	
	/**
	 * Get the client associated with an entity
	 * @return The client associated with this entity
	 */
	public ChatClient getClient();
}
