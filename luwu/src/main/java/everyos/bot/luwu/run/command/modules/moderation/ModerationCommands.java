package everyos.bot.luwu.run.command.modules.moderation;

import everyos.bot.luwu.core.command.CommandContainer;

public final class ModerationCommands {
	private ModerationCommands() {}
	
	public static void installTo(CommandContainer container) {
		container.registerCommand("command.ban", BanCommand.get());
		container.registerCommand("command.kick", KickCommand.get());
	}
}
