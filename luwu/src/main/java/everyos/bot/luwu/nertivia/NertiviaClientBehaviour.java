package everyos.bot.luwu.nertivia;

import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.client.ClientBehaviour;

public class NertiviaClientBehaviour implements ClientBehaviour {
	@Override public ArgumentParser createParser(String argument) {
		return new NertiviaArgumentParser(argument);
	}
}
