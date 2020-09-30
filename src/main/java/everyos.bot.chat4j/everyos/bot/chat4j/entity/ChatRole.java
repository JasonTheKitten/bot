package everyos.bot.chat4j.entity;

import everyos.bot.chat4j.functionality.ChatInterfaceProvider;

public interface ChatRole extends ChatInterfaceProvider {
	/**
	 * Get an ID representing this role
	 * @return An ID representing this role
	 */
	public long getID();
	//TODO: Perhaps make ID a class
	
	/**
	 * Get this role's name
	 * @return This role's name
	 */
	public String getName();
}
