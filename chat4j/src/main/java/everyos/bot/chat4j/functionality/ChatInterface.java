package everyos.bot.chat4j.functionality;

import everyos.bot.chat4j.ChatClient;
import everyos.bot.chat4j.ChatConnection;

public interface ChatInterface {
	ChatConnection getConnection();
	ChatClient getClient();
}
