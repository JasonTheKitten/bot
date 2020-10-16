package everyos.bot.luwu.run.command.modules.moderation;

import everyos.bot.luwu.core.entity.Channel;
import everyos.bot.luwu.core.entity.Member;
import everyos.bot.luwu.core.entity.Server;
import reactor.core.publisher.Mono;

final class LoggedModerationAction {
	private LoggedModerationAction() {}
	
	protected static Mono<Void> log(String name, Channel channel, Member invoker, Member member, String reason) {
		return Mono.empty();
	}
	protected static Mono<Void> log(String name, Server server, Member invoker, Member member, String reason) {
		return Mono.empty();
	}
}
