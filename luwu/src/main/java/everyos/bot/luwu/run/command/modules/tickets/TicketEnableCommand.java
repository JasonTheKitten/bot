package everyos.bot.luwu.run.command.modules.tickets;

import everyos.bot.chat4j.entity.ChatPermission;
import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.CommandData;
import everyos.bot.luwu.run.command.CommandBase;
import reactor.core.publisher.Mono;

public class TicketEnableCommand extends CommandBase {
	public TicketEnableCommand(boolean disable) {
		super("command.ticket.enable", e->true,
			ChatPermission.SEND_MESSAGES | ChatPermission.MANAGE_CHANNELS,
			ChatPermission.MANAGE_CHANNELS);
	}

	@Override
	public Mono<Void> execute(CommandData data, ArgumentParser parser) {
		// TODO Auto-generated method stub
		return null;
	}
}
