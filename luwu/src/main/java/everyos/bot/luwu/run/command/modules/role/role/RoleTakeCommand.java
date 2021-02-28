package everyos.bot.luwu.run.command.modules.role.role;

import java.util.Optional;

import everyos.bot.chat4j.entity.ChatPermission;
import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.CommandData;
import everyos.bot.luwu.core.entity.Channel;
import everyos.bot.luwu.core.entity.Locale;
import everyos.bot.luwu.core.entity.Member;
import everyos.bot.luwu.core.entity.RoleID;
import everyos.bot.luwu.core.exception.TextException;
import everyos.bot.luwu.core.functionality.channel.ChannelTextInterface;
import everyos.bot.luwu.core.functionality.member.MemberRoleInterface;
import everyos.bot.luwu.run.command.CommandBase;
import reactor.core.publisher.Mono;

public class RoleTakeCommand extends CommandBase {

	private boolean isUntake;

	public RoleTakeCommand(boolean isUntake) {
		super(isUntake?"command.role.role.untake":"command.role.role.take", e->true,
			ChatPermission.SEND_MESSAGES | ChatPermission.MANAGE_ROLES,
			ChatPermission.NONE);
		this.isUntake = isUntake;
	}

	@Override
	public Mono<Void> execute(CommandData data, ArgumentParser parser) {
		Locale locale = data.getLocale();
		
		return
			parseArgs(parser, locale)
			.flatMap(roleName->runCommand(data.getChannel(), roleName, data.getInvoker(), locale));
	}

	private Mono<String> parseArgs(ArgumentParser parser, Locale locale) {
		if (parser.isEmpty()) return expect(locale, parser, "command.error.string");
		String roleName = parser.eat();
		return Mono.just(roleName);
	}

	private Mono<Void> runCommand(Channel channel, String roleName, Member invoker, Locale locale) {
		return channel.getServer()
			.flatMap(server->server.as(RoleServer.type))
			.flatMap(server->server.getInfo())
			.flatMap(info->{
				Optional<RoleID> role = info.getRole(roleName);
				if (role.isEmpty()) {
					return Mono.error(new TextException(locale.localize("command.role.role.take.error.nosuchrole")));
				}
				MemberRoleInterface intf = invoker.getInterface(MemberRoleInterface.class);
				if (isUntake) {
					return intf.addRole(role.get(), locale.localize("command.role.role.untake.invoked"));
				} else {
					return intf.addRole(role.get(), locale.localize("command.role.role.take.invoked"));
				}
			})
			.then(channel.getInterface(ChannelTextInterface.class).send(locale.localize(isUntake?
					"command.role.role.untake.message":
					"command.role.role.take.message")))
			.then();
	}
}
