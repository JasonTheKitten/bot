package everyos.bot.luwu.run.command.modules.tickets;

import everyos.bot.luwu.core.command.Command;
import everyos.bot.luwu.core.command.CommandContainer;
import everyos.bot.luwu.run.command.MultiCommand;

public class TicketCommand extends MultiCommand {
	private CommandContainer commands;

	public TicketCommand() {
		super("command.ticket");
		
		this.commands = new CommandContainer();
		
		Command ticketCreateCommand = new TicketCreateCommand();
		
		commands.registerCommand("command.ticket.create", ticketCreateCommand);
		//TODO: Enable command
	}

	@Override
	public CommandContainer getCommands() {
		return commands;
	}
}
