package everyos.bot.luwu.command.modules.moderation;

import everyos.bot.luwu.command.CommandContainer;

public final class ModerationCommands {
	private ModerationCommands() {}
	
	public static void installTo(CommandContainer container) {
		container.registerCommand("command.ban", BanCommand.get());
		container.registerCommand("command.kick", KickCommand.get());
	}
}
