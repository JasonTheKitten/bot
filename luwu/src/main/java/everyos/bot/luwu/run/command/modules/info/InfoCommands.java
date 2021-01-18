package everyos.bot.luwu.run.command.modules.info;

import everyos.bot.luwu.core.command.CommandContainer;

public class InfoCommands {
	public static void installTo(CommandContainer commands) {
		commands.registerCommand("command.donate", new DonateCommand());
		commands.registerCommand("command.help", new HelpCommand());
	}
}
