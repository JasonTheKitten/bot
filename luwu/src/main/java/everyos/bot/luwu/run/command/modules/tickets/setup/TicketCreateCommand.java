package everyos.bot.luwu.run.command.modules.tickets.setup;

import everyos.bot.chat4j.entity.ChatPermission;
import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.CommandData;
import everyos.bot.luwu.core.entity.Channel;
import everyos.bot.luwu.core.entity.Locale;
import everyos.bot.luwu.core.entity.Member;
import everyos.bot.luwu.core.exception.TextException;
import everyos.bot.luwu.run.command.CommandBase;
import everyos.bot.luwu.run.command.modules.tickets.TicketServer;
import reactor.core.publisher.Mono;

public class TicketCreateCommand extends CommandBase {
	public TicketCreateCommand() {
		super("command.ticket.create", e->true, ChatPermission.SEND_MESSAGES|ChatPermission.MANAGE_CHANNELS, ChatPermission.SEND_MESSAGES);
	}

	@Override
	public Mono<Void> execute(CommandData data, ArgumentParser parser) {
		Locale locale = data.getLocale();
		
		return
			checkEnabled(data.getChannel(), locale)
			.then(runCommand(data.getInvoker(), data.getChannel(), locale));
	}

	private Mono<Void> checkEnabled(Channel channel, Locale locale) {
		return channel.getServer()
			.flatMap(server->server.as(TicketServer.type))
			.flatMap(server->server.getInfo())
			.flatMap(server->{
				if (!server.getEnabled()) {
					return Mono.error(new TextException(locale.localize("command.ticket.create.disabled")));
				}
				return Mono.empty();
			})
			.then();
	}
	
	private Mono<Void> runCommand(Member invoker, Channel channel, Locale locale) {
		return channel.getServer()
			.flatMap(server->server.createChannel(spec->{
				spec.setName(locale.localize("command.ticket.create.name")+'-'+invoker.getHumanReadableID().substring(0, 15));
			}))
			.then();
	}
}
