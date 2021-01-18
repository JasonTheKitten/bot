package everyos.bot.luwu.run.command.modules.chatlink.setup;

import everyos.bot.luwu.core.command.CommandContainer;
import everyos.bot.luwu.run.command.MultiCommand;

public class LinkCommand extends MultiCommand {
	private CommandContainer commands;

	public LinkCommand() {
		super("command.link.setup");
		
		CommandContainer commands = new CommandContainer();
		
		commands.registerCommand("command.link.create", new LinkCreateCommand());
		commands.registerCommand("command.link.join", new LinkJoinCommand());
		
		this.commands = commands;
	}

	@Override
	public CommandContainer getCommands() {
		return commands;
	}
}
