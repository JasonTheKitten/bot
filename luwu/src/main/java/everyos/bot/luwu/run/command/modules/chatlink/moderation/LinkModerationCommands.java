package everyos.bot.luwu.run.command.modules.chatlink.moderation;

import everyos.bot.luwu.core.command.CommandContainer;

public class LinkModerationCommands {
	public static void installTo(CommandContainer commands) {
		commands.registerCommand("command.link", new LinkCommand());
	}
}
