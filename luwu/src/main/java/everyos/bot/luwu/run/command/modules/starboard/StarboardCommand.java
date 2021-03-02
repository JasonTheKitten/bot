package everyos.bot.luwu.run.command.modules.starboard;

import everyos.bot.luwu.core.command.CommandContainer;
import everyos.bot.luwu.run.command.MultiCommand;

public class StarboardCommand extends MultiCommand {
	public StarboardCommand(String id) {
		super("command.starboard");
	}

	@Override
	public CommandContainer getCommands() {
		CommandContainer commands = new CommandContainer();
		
		commands.registerCommand("command.starboard.set", new StarboardSetCommand());
		
		return commands;
	}
}
