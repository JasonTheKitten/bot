package everyos.bot.luwu.run.command.modules.logging;

import everyos.bot.luwu.core.command.CommandContainer;

public final class LogsCommands {
	private LogsCommands() {}
	
	public static void installTo(CommandContainer commands) {
		commands.registerCommand("command.logs", new LogsCommand());
	}
}
