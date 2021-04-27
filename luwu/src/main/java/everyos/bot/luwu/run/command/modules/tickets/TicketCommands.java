package everyos.bot.luwu.run.command.modules.tickets;

import everyos.bot.luwu.core.command.CommandContainer;
import everyos.bot.luwu.run.command.modules.tickets.setup.TicketCommand;

public class TicketCommands {
	public static void installTo(CommandContainer container) {
		container.registerCommand("command.ticket", new TicketCommand());
	}
}
