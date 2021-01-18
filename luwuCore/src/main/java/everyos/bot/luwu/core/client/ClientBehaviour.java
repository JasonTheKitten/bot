package everyos.bot.luwu.core.client;

import everyos.bot.luwu.core.entity.Connection;

public interface ClientBehaviour {
	ArgumentParser createParser(Connection connection, String argument);
}
