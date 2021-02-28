package everyos.bot.luwu.run.command.modules.role;

import everyos.bot.luwu.core.command.CommandContainer;
import everyos.bot.luwu.run.command.modules.role.reaction.ReactionCommand;
import everyos.bot.luwu.run.command.modules.role.role.RoleCommand;

public final class RoleCommands {
	private RoleCommands() {}
	
	public static void installTo(CommandContainer container) {
		container.registerCommand("command.role.reaction", new ReactionCommand());
		container.registerCommand("command.role.role", new RoleCommand());
	}
}
