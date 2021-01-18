package everyos.bot.luwu.run.command.modules.easteregg;

import everyos.bot.luwu.core.command.CommandContainer;

public class EasterEggCommands {
	public static void installTo(CommandContainer commands) {
		commands.registerCommand("command.easteregg.cat", new CatCommand());
		commands.registerCommand("command.easteregg.cart", new CartCommand());
		commands.registerCommand("command.easteregg.uwu", new UwUCommand());
	}
}