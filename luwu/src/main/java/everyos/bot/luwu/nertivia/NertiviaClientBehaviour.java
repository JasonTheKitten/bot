package everyos.bot.luwu.nertivia;

import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.client.ClientBehaviour;
import everyos.bot.luwu.core.entity.Connection;

public class NertiviaClientBehaviour implements ClientBehaviour {
	@Override
	public ArgumentParser createParser(Connection connection, String argument) {
		return new NertiviaArgumentParser(connection, argument);
	}
}
