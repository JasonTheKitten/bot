package everyos.bot.chat4j.entity;

import everyos.bot.chat4j.functionality.ChatInterfaceProvider;

public interface ChatGuild  extends ChatInterfaceProvider {
	/**
	 * Get an ID representing this guild
	 * @return An ID representing this guild
	 */
	public long getID();
}
