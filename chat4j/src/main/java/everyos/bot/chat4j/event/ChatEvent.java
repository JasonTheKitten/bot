package everyos.bot.chat4j.event;

import everyos.bot.chat4j.ChatClient;
import everyos.bot.chat4j.ChatConnection;
import everyos.bot.chat4j.ChatShard;

public interface ChatEvent {
	public ChatClient getClient();
	public ChatConnection getConnection();
	
	public ChatShard getShard();
}
