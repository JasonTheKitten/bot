package everyos.bot.luwu.client;

import everyos.bot.luwu.parser.ArgumentParser;

public interface ClientBehaviour {
	public ArgumentParser createParser(String argument);
}
