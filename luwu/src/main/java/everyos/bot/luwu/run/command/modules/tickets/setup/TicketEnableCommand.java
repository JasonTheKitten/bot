package everyos.bot.luwu.run.command.modules.tickets.setup;

import everyos.bot.chat4j.entity.ChatPermission;
import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.CommandData;
import everyos.bot.luwu.core.entity.Channel;
import everyos.bot.luwu.core.entity.Locale;
import everyos.bot.luwu.core.functionality.channel.ChannelTextInterface;
import everyos.bot.luwu.run.command.CommandBase;
import everyos.bot.luwu.run.command.modules.tickets.server.TicketServer;
import reactor.core.publisher.Mono;

public class TicketEnableCommand extends CommandBase {
	private boolean disable;

	public TicketEnableCommand(boolean disable) {
		super(disable?"command.ticket.disable":"command.ticket.enable", e->true,
			ChatPermission.SEND_MESSAGES | ChatPermission.MANAGE_CHANNELS,
			ChatPermission.MANAGE_CHANNELS);
		this.disable = disable;
	}

	@Override
	public Mono<Void> execute(CommandData data, ArgumentParser parser) {
		Locale locale = data.getLocale();
		
		return
			runCommand(data.getChannel(), locale);
	}

	private Mono<Void> runCommand(Channel channel, Locale locale) {
		return channel
			.getServer()
			.flatMap(server->server.as(TicketServer.type))
			.flatMap(server->server.edit(spec->spec.setEnabled(!disable)))
			.then(channel.getInterface(ChannelTextInterface.class)
				.send(locale.localize(disable?"command.ticket.disable.message":"command.ticket.enable.message")))
			.then();
	}
}
