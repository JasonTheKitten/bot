package everyos.bot.luwu.run.command;

import everyos.bot.luwu.core.entity.Member;
import reactor.core.publisher.Mono;

public interface RateLimit {
	Mono<Void> checkRateLimit(Member member);
}
