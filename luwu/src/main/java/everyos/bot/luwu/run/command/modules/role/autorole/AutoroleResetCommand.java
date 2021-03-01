package everyos.bot.luwu.run.command.modules.role.autorole;

import everyos.bot.chat4j.entity.ChatPermission;
import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.CommandData;
import everyos.bot.luwu.core.entity.Channel;
import everyos.bot.luwu.core.entity.Locale;
import everyos.bot.luwu.core.functionality.channel.ChannelTextInterface;
import everyos.bot.luwu.run.command.CommandBase;
import reactor.core.publisher.Mono;

public class AutoroleResetCommand extends CommandBase {

	public AutoroleResetCommand() {
		super("command.role.autorole.reset", e->true,
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
			.flatMap(server->server.as(AutoroleServer.type))
			.flatMap(server->server.edit(spec->{
				spec.reset();
				
				return Mono.empty();
			}))
			.then(channel.getInterface(ChannelTextInterface.class).send(locale.localize("command.role.autorole.reset.message")))
			.then();
	}
		
}
