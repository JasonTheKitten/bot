package everyos.bot.luwu.run.command.modules.privacy;

import everyos.bot.luwu.core.entity.event.ServerDeleteEvent;
import reactor.core.publisher.Mono;

public class PrivacyHooks {
	
	public static Mono<Void> privacyHook(ServerDeleteEvent e) {
		return e.getServer()
			.flatMap(server->server.wipe());
	}
	
}
