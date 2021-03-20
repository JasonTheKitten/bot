package everyos.bot.luwu.run.command.modules.starboard;

import everyos.bot.luwu.core.command.CommandContainer;

public class StarboardCommands {
	public static void installTo(CommandContainer commands) {
		commands.registerCommand("command.starboard", new StarboardCommand());
	}
}
