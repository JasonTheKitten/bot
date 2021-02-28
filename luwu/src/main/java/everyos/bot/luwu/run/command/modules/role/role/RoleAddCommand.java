package everyos.bot.luwu.run.command.modules.role.role;

import everyos.bot.chat4j.entity.ChatPermission;
import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.CommandData;
import everyos.bot.luwu.core.entity.Channel;
import everyos.bot.luwu.core.entity.Locale;
import everyos.bot.luwu.core.entity.RoleID;
import everyos.bot.luwu.core.functionality.channel.ChannelTextInterface;
import everyos.bot.luwu.run.command.CommandBase;
import everyos.bot.luwu.util.Tuple;
import reactor.core.publisher.Mono;

public class RoleAddCommand extends CommandBase {

	public RoleAddCommand() {
		super("command.role.role.add", e->true,
			ChatPermission.SEND_MESSAGES | ChatPermission.MANAGE_ROLES,
			ChatPermission.MANAGE_ROLES);
	}

	@Override
	public Mono<Void> execute(CommandData data, ArgumentParser parser) {
		Locale locale = data.getLocale();
		
		return
			parseArgs(parser, locale)
			.flatMap(tup->runCommand(data.getChannel(), tup.getT1(), tup.getT2(), locale));
	}

	private Mono<Tuple<String, RoleID>> parseArgs(ArgumentParser parser, Locale locale) {
		if (parser.isEmpty()) return expect(locale, parser, "command.error.string");
		String roleName = parser.eat();
		if (!parser.couldBeRoleID()) return expect(locale, parser, "command.error.roleid");
		RoleID roleID = parser.eatRoleID();
		return Mono.just(Tuple.of(roleName, roleID));
	}

	private Mono<Void> runCommand(Channel channel, String roleName, RoleID roleID, Locale locale) {
		return channel.getServer()
			.flatMap(server->server.as(RoleServer.type))
			.flatMap(server->server.edit(spec->{
				spec.setRole(roleName, roleID);
			}))
			.then(channel.getInterface(ChannelTextInterface.class).send(locale.localize("command.role.role.add.message")))
			.then();
	}
}
