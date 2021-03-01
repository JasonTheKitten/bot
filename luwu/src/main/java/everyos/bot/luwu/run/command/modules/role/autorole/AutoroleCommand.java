package everyos.bot.luwu.run.command.modules.role.autorole;

import everyos.bot.luwu.core.command.CommandContainer;
import everyos.bot.luwu.run.command.MultiCommand;

public class AutoroleCommand extends MultiCommand {
	public AutoroleCommand() {
		super("command.role.autorole");
	}

	@Override
	public CommandContainer getCommands() {
		CommandContainer commands = new CommandContainer();
		
		commands.registerCommand("command.role.autorole.add", new AutoroleAddCommand());
		commands.registerCommand("command.role.autorole.remove", new AutoroleRemoveCommand());
		commands.registerCommand("command.role.autorole.reset", new AutoroleResetCommand());
		commands.registerCommand("command.role.autorole.setbotrole", new AutoroleSetSpecialCommand(true));
		commands.registerCommand("command.role.autorole.setuserrole", new AutoroleSetSpecialCommand(false));
		commands.registerCommand("command.role.autorole.list", new AutoroleListCommand());
		commands.registerCommand("command.role.autorole.reset", new AutoroleResetCommand());
		
		return commands;
	}
}
