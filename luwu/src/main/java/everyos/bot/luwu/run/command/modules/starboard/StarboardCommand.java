package everyos.bot.luwu.run.command.modules.starboard;

import everyos.bot.luwu.core.command.CommandContainer;
import everyos.bot.luwu.run.command.MultiCommand;

public class StarboardCommand extends MultiCommand {
	public StarboardCommand() {
		super("command.starboard");
	}

	@Override
	public CommandContainer getCommands() {
		CommandContainer commands = new CommandContainer();
		
		commands.registerCommand("command.starboard.set", new StarboardSetCommand());
		commands.registerCommand("command.starboard.list", new StarboardListCommand());
		commands.registerCommand("command.starboard.add", new StarboardAddCommand());
		commands.registerCommand("command.starboard.remove", new StarboardRemoveCommand());
		commands.registerCommand("command.starboard.unset", new StarboardUnsetCommand());
		//commands.registerCommand("command.starboard.ignore", new StarboardIgnoreCommand());
		//commands.registerCommand("command.starboard.unignore", new StarboardUnignoreCommand());
		
		return commands;
	}
}
