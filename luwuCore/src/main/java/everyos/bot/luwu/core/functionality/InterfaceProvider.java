package everyos.bot.luwu.core.functionality;

import everyos.bot.chat4j.functionality.UnsupportedInterfaceException;
import everyos.bot.luwu.core.entity.Client;

public interface InterfaceProvider {
	/**
	 * Check if this entity supports the given functionality
	 * @param <T> The functionality to query support for
	 * @param cls The interface of the functionality to query support for
	 * @return A boolean indicating whether the functionality is supported
	 */
	public <T extends Interface> boolean supportsInterface(Class<T> cls);
	
	/**
	 * Get an endpoint of the client
	 * @param <T> The functionality to get an instance of
	 * @param cls The interface of the functionality to get an instance of
	 * @return An instance of the specified fuctionality
	 * @throws UnsupportedInterfaceException Thrown if the functionality is not supported
	 */
	public <T extends Interface> T getInterface(Class<T> cls);
	
	/**
	 * Get the client associated with an entity
	 * @return The client associated with this entity
	 */
	public Client getClient();
}