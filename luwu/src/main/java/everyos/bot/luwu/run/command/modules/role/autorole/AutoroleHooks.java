package everyos.bot.luwu.run.command.modules.role.autorole;

import everyos.bot.luwu.core.entity.Member;
import everyos.bot.luwu.core.entity.RoleID;
import everyos.bot.luwu.core.entity.event.MemberJoinEvent;
import everyos.bot.luwu.core.functionality.member.MemberRoleInterface;
import reactor.core.publisher.Mono;

public final class AutoroleHooks {
	private AutoroleHooks() {}
	
	public static Mono<Void> autoroleHook(MemberJoinEvent event) {
		return event.getServer()
			.flatMap(server->server.as(AutoroleServer.type))
			.flatMap(server->server.getInfo())
			.flatMap(info->{
				Mono<Void> chain = Mono.empty();
				Member newWelcome = event.getMember();
				MemberRoleInterface newWelcomeI = newWelcome.getInterface(MemberRoleInterface.class);
				
				if (newWelcome.isBot() && info.getBotRole().isPresent()) {
					chain = chain.and(newWelcomeI.addRole(info.getBotRole().get(), "Autorole")); //TODO: Localize
				}
				if (!newWelcome.isBot() && info.getUserRole().isPresent()) {
					chain = chain.and(newWelcomeI.addRole(info.getUserRole().get(), "Autorole"));
				}
				for (RoleID autorole: info.getDefaultRoles()) {
					chain = chain.and(newWelcomeI.addRole(autorole, "Autorole"));
				}
				
				return chain;
			})
			.then();
	}
}
