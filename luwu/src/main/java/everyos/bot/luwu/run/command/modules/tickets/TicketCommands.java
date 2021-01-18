package everyos.bot.luwu.run.command.modules.tickets;

import everyos.bot.luwu.core.command.CommandContainer;

public class TicketCommands {
	public void installTo(CommandContainer container) {
		container.registerCommand("command.ticket", new TicketCommand());
	}
}
