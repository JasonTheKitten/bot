package everyos.bot.luwu.run.command.modules.role.autorole;

import everyos.bot.chat4j.entity.ChatPermission;
import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.CommandData;
import everyos.bot.luwu.core.entity.Channel;
import everyos.bot.luwu.core.entity.Locale;
import everyos.bot.luwu.core.entity.RoleID;
import everyos.bot.luwu.core.exception.TextException;
import everyos.bot.luwu.core.functionality.channel.ChannelTextInterface;
import everyos.bot.luwu.run.command.CommandBase;
import reactor.core.publisher.Mono;

public class AutoroleRemoveCommand extends CommandBase {

	public AutoroleRemoveCommand() {
		super("command.role.autorole.remove", e->true,
			ChatPermission.SEND_MESSAGES | ChatPermission.MANAGE_ROLES,
			ChatPermission.MANAGE_ROLES);
	}

	@Override
	public Mono<Void> execute(CommandData data, ArgumentParser parser) {
		Locale locale = data.getLocale();
		
		return 
			parseArgs(parser, locale)
			.flatMap(role->runCommand(data.getChannel(), role, locale));
	}

	private Mono<RoleID> parseArgs(ArgumentParser parser, Locale locale) {
		if (!parser.couldBeRoleID()) {
			return expect(locale, parser, "command.error.roleid");
		}
		return Mono.just(parser.eatRoleID());
	}
	
	private Mono<Void> runCommand(Channel channel, RoleID roleID, Locale locale) {
		return channel.getServer()
			.flatMap(server->server.as(AutoroleServer.type))
			.flatMap(server->server.edit(spec->{
				if (!spec.getInfo().hasRole(roleID)) {
					return Mono.error(new TextException(locale.localize("command.role.autorole.remove.noneto")));
				};
				
				spec.removeRole(roleID);
				
				return Mono.empty();
			}))
			.then(channel.getInterface(ChannelTextInterface.class).send(locale.localize("command.role.autorole.remove.message")))
			.then();
	}
		
}
