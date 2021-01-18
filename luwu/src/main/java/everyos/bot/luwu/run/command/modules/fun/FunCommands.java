package everyos.bot.luwu.run.command.modules.fun;

import everyos.bot.luwu.core.command.CommandContainer;

public class FunCommands {
	public static void installTo(CommandContainer commands) {
		commands.registerCommand("command.giphy", new GiphyCommand());
		commands.registerCommand("command.hug", new HugCommand());
	}
}