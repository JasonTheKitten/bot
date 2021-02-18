package everyos.bot.luwu.run.command.modules.battle;

import everyos.bot.luwu.core.command.CommandContainer;

public class AdventureCommands {
	public static void installTo(CommandContainer commands) {
		commands.registerCommand("command.battle", BattleCommand.get());
	}
}
