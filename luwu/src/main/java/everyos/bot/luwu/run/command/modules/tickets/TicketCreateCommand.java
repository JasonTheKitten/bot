package everyos.bot.luwu.run.command.modules.tickets;

import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.CommandData;
import everyos.bot.luwu.run.command.CommandBase;
import reactor.core.publisher.Mono;

public class TicketCreateCommand extends CommandBase {
	public TicketCreateCommand() {
		super("command.ticket.create");
	}

	@Override
	public Mono<Void> execute(CommandData data, ArgumentParser parser) {
		// TODO Auto-generated method stub
		return null;
	}
}
