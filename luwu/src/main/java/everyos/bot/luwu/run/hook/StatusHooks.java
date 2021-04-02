package everyos.bot.luwu.run.hook;

import everyos.bot.luwu.core.entity.event.ServerEvent;
import reactor.core.publisher.Mono;

public class StatusHooks {
	public static Mono<Void> statusHook(ServerEvent event) {
		return Mono.empty();
	}
}
