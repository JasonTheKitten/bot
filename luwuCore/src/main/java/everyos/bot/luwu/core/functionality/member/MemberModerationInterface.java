package everyos.bot.luwu.core.functionality.member;

import everyos.bot.luwu.core.functionality.Interface;
import reactor.core.publisher.Mono;

public interface MemberModerationInterface extends Interface {
	public Mono<Void> ban(String reason);
	public Mono<Void> kick(String reason);
}
