package everyos.bot.luwu.run.command.modules.chatlink.setup;

import everyos.bot.luwu.core.command.CommandContainer;

public class LinkSetupCommands {
	public static void installTo(CommandContainer commands) {
		commands.registerCommand("command.link.setup", new LinkCommand());
	}
}
