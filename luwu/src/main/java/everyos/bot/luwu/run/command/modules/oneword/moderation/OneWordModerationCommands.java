package everyos.bot.luwu.run.command.modules.oneword.moderation;

import everyos.bot.luwu.core.command.CommandContainer;
import everyos.bot.luwu.run.command.modules.moderation.ModerationCommands;

public final class OneWordModerationCommands {
	private OneWordModerationCommands() {}
	
	public static void installTo(CommandContainer commands) {
		commands.registerCommand("command.oneword.moderation", new OneWordModerationCommand());
		
		commands.category("moderation");
		ModerationCommands.installTo(commands);
	}
}
