package everyos.bot.luwu.core.entity;

import everyos.bot.chat4j.entity.ChatGuild;
import everyos.bot.luwu.core.functionality.Interface;
import everyos.bot.luwu.core.functionality.InterfaceProvider;

public class Server implements InterfaceProvider {
	public Server(ChatGuild guild) {
		
	}

	public ServerID getID() {
		return null;
	}

	@Override public <T extends Interface> boolean supportsInterface(Class<T> cls) {
		return false;
	}

	@Override public <T extends Interface> T getInterface(Class<T> cls) {
		return null;
	}

	@Override public Client getClient() {
		return null;
	}
}
