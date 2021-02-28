package everyos.bot.luwu.run.command.modules.role.role;

import everyos.bot.chat4j.entity.ChatPermission;
import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.CommandData;
import everyos.bot.luwu.core.entity.Channel;
import everyos.bot.luwu.core.entity.Locale;
import everyos.bot.luwu.core.functionality.channel.ChannelTextInterface;
import everyos.bot.luwu.run.command.CommandBase;
import reactor.core.publisher.Mono;

public class RoleResetCommand extends CommandBase {

	public RoleResetCommand() {
		super("command.role.role.reset", e->true,
				ChatPermission.SEND_MESSAGES | ChatPermission.MANAGE_ROLES,
				ChatPermission.MANAGE_ROLES);
	}

	@Override
	public Mono<Void> execute(CommandData data, ArgumentParser parser) {
		Locale locale = data.getLocale();
		
		return
			runCommand(data.getChannel(), locale);
	}

	private Mono<Void> runCommand(Channel channel, Locale locale) {
		return channel.getServer()
			.flatMap(server->server.as(RoleServer.type))
			.flatMap(server->server.edit(spec->{
				spec.reset();
			}))
			.then(channel.getInterface(ChannelTextInterface.class).send(locale.localize("command.role.role.reset.message")))
			.then();
	}
}
