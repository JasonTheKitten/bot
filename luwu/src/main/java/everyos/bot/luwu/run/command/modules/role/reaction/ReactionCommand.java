package everyos.bot.luwu.run.command.modules.role.reaction;

import everyos.bot.luwu.core.command.Command;
import everyos.bot.luwu.core.command.CommandContainer;
import everyos.bot.luwu.run.command.MultiCommand;

public class ReactionCommand extends MultiCommand {
	private CommandContainer commands;

	public ReactionCommand() {
		super("command.role.reaction");
		
		this.commands = new CommandContainer();
		
		Command reactionAddCommand = new ReactionAddCommand();
		
		commands.registerCommand("command.role.reaction.add", reactionAddCommand);
	}

	@Override
	public CommandContainer getCommands() {
		return commands;
	}
}
