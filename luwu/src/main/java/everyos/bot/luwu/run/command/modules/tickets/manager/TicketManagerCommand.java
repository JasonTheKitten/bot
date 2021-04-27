package everyos.bot.luwu.run.command.modules.tickets.manager;

import everyos.bot.luwu.core.command.CommandContainer;
import everyos.bot.luwu.run.command.MultiCommand;

public class TicketManagerCommand extends MultiCommand {
	public TicketManagerCommand() {
		super("command.ticket.manager");
	}

	@Override
	public CommandContainer getCommands() {
		CommandContainer commands = new CommandContainer();
		
		commands.registerCommand("command.ticket.manager.close", new TicketManagerCloseCommand());
		
		return commands;
	}
}
