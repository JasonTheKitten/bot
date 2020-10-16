
package everyos.bot.luwu.discord;

import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.client.ClientBehaviour;

public class DiscordClientBehaviour implements ClientBehaviour {
	@Override public ArgumentParser createParser(String argument) {
		return new DiscordArgumentParser(argument);
	}
}
