package everyos.bot.luwu.run.command.modules.welcome;

import everyos.bot.luwu.core.entity.event.MemberEvent;
import reactor.core.publisher.Mono;

public class WelcomeHooks {
	public static Mono<Void> welcomeHook(MemberEvent e) {
		return sendWelcomeMessage(e, true);
	}

	private static Mono<Void> sendWelcomeMessage(MemberEvent e, boolean isWelcome) {
		e.getMember().getServer()
		.flatMap(server->server.as(WelcomeServer.type))
		.flatMap(server->{
			return Mono.empty();
		});
		return Mono.empty();
	}
}
