package everyos.bot.luwu.run.command.modules.role.reaction;

import everyos.bot.luwu.core.entity.event.ReactionEvent;
import reactor.core.publisher.Mono;

public class ReactionHooks {
	public static Mono<Void> reactionHook(ReactionEvent event) {
		return Mono.empty();
	}
}
