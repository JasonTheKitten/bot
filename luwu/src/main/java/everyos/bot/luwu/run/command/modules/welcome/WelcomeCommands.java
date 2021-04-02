package everyos.bot.luwu.run.command.modules.welcome;

import everyos.bot.luwu.core.command.CommandContainer;

public final class WelcomeCommands {
	private WelcomeCommands() {}
	
	public static void installTo(CommandContainer container) {
		container.registerCommand("command.welcome", new WelcomeCommand(true));
		container.registerCommand("command.leave", new WelcomeCommand(false));
	}
}
