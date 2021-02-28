package everyos.bot.luwu.run.command.modules.role.role;

import everyos.bot.luwu.core.command.CommandContainer;
import everyos.bot.luwu.run.command.MultiCommand;

public class RoleCommand extends MultiCommand {
	public RoleCommand() {
		super("command.role.role");
	}

	@Override
	public CommandContainer getCommands() {
		CommandContainer commands = new CommandContainer();
		
		commands.registerCommand("command.role.role.add", new RoleAddCommand());
		commands.registerCommand("command.role.role.remove", new RoleRemoveCommand());
		commands.registerCommand("command.role.role.take", new RoleTakeCommand(false));
		commands.registerCommand("command.role.role.untake", new RoleTakeCommand(true));
		commands.registerCommand("command.role.role.reset", new RoleResetCommand());
		commands.registerCommand("command.role.role.list", new RoleListCommand());
		
		return commands;
	}

}
