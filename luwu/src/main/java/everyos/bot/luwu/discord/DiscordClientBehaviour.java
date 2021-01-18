
package everyos.bot.luwu.discord;

import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.client.ClientBehaviour;
import everyos.bot.luwu.core.entity.Connection;

public class DiscordClientBehaviour implements ClientBehaviour {
	@Override
	public ArgumentParser createParser(Connection connection, String argument) {
		return new DiscordArgumentParser(connection, argument);
	}
}
