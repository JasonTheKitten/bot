package everyos.bot.luwu.run.command.modules.configuration.prefix;

import everyos.bot.luwu.core.command.CommandContainer;
import everyos.bot.luwu.run.command.MultiCommand;

public class PrefixCommand extends MultiCommand {
	
	public PrefixCommand() {
		super("command.prefix");
	}

	@Override
	public CommandContainer getCommands() {
		CommandContainer commands = new CommandContainer();
		
		commands.registerCommand("command.prefix.add", new PrefixAddCommand(false));
		commands.registerCommand("command.prefix.remove", new PrefixAddCommand(true));
		commands.registerCommand("command.prefix.list", new PrefixListCommand());
		
		return commands;
	}

}
