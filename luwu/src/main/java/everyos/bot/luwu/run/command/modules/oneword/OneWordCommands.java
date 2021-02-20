package everyos.bot.luwu.run.command.modules.oneword;

import everyos.bot.luwu.core.command.CommandContainer;
import everyos.bot.luwu.run.command.modules.oneword.setup.OneWordCommand;

public final class OneWordCommands {
	private OneWordCommands() {}
	
	public static void installTo(CommandContainer commands) {
		commands.registerCommand("command.oneword.setup", new OneWordCommand());
	}
}
