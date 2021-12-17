package everyos.bot.luwu.run.command.modules.configuration;

import everyos.bot.luwu.core.command.CommandContainer;
import everyos.bot.luwu.run.command.modules.configuration.prefix.PrefixCommand;

public final class ConfigurationCommands {
	
	private ConfigurationCommands() {}
	
	public static void installTo(CommandContainer container) {
		container.registerCommand("command.prefix", new PrefixCommand());
	}
	
}
