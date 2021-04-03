package everyos.bot.luwu.run.command.modules.currency;

import everyos.bot.luwu.core.command.CommandContainer;

public class FethCommands {
	public static void installTo(CommandContainer commands) {
		commands.registerCommand("command.feth", new FethCommand());
	}
}
