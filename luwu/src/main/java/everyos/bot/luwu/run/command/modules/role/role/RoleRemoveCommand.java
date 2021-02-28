package everyos.bot.luwu.run.command.modules.role.role;

import everyos.bot.chat4j.entity.ChatPermission;
import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.CommandData;
import everyos.bot.luwu.core.entity.Channel;
import everyos.bot.luwu.core.entity.Locale;
import everyos.bot.luwu.core.functionality.channel.ChannelTextInterface;
import everyos.bot.luwu.run.command.CommandBase;
import reactor.core.publisher.Mono;

public class RoleRemoveCommand extends CommandBase {

	public RoleRemoveCommand() {
		super("command.role.role.remove", e->true,
			ChatPermission.SEND_MESSAGES | ChatPermission.MANAGE_ROLES,
			ChatPermission.MANAGE_ROLES);
	}

	@Override
	public Mono<Void> execute(CommandData data, ArgumentParser parser) {
		Locale locale = data.getLocale();
		
		return
			parseArgs(parser, locale)
			.flatMap(roleName->runCommand(data.getChannel(), roleName, locale));
	}

	private Mono<String> parseArgs(ArgumentParser parser, Locale locale) {
		if (parser.isEmpty()) return expect(locale, parser, "command.error.string");
		String roleName = parser.eat();
		return Mono.just(roleName);
	}

	private Mono<Void> runCommand(Channel channel, String roleName, Locale locale) {
		return channel.getServer()
			.flatMap(server->server.as(RoleServer.type))
			.flatMap(server->server.edit(spec->{
				spec.removeRole(roleName);
			}))
			.then(channel.getInterface(ChannelTextInterface.class).send(locale.localize("command.role.role.remove.message")))
			.then();
	}
}
