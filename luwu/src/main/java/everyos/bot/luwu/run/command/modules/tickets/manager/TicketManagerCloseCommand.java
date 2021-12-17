package everyos.bot.luwu.run.command.modules.tickets.manager;

import everyos.bot.chat4j.entity.ChatPermission;
import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.CommandData;
import everyos.bot.luwu.core.entity.Channel;
import everyos.bot.luwu.run.command.CommandBase;
import reactor.core.publisher.Mono;

public class TicketManagerCloseCommand extends CommandBase {
	
	public TicketManagerCloseCommand() {
		super("command.ticket.manager.close", e->true, ChatPermission.SEND_MESSAGES, ChatPermission.NONE);
	}

	@Override
	public Mono<Void> execute(CommandData data, ArgumentParser parser) {
		return
			runCommand(data.getChannel());
	}

	private Mono<Void> runCommand(Channel channel) {
		return channel.delete();
	}
	
}
