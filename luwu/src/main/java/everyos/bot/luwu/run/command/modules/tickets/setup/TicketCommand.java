package everyos.bot.luwu.run.command.modules.tickets.setup;

import everyos.bot.luwu.core.command.Command;
import everyos.bot.luwu.core.command.CommandContainer;
import everyos.bot.luwu.run.command.MultiCommand;

public class TicketCommand extends MultiCommand {
	private CommandContainer commands;

	public TicketCommand() {
		super("command.ticket");
		
		this.commands = new CommandContainer();
		
		Command ticketCreateCommand = new TicketCreateCommand();
		Command ticketEnableCommand = new TicketEnableCommand(false);
		Command ticketDisableCommand = new TicketEnableCommand(true);
		
		commands.registerCommand("command.ticket.create", ticketCreateCommand);
		commands.registerCommand("command.ticket.enable", ticketEnableCommand);
		commands.registerCommand("command.ticket.disable", ticketDisableCommand);
	}

	@Override
	public CommandContainer getCommands() {
		return commands;
	}
}
