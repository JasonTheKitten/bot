package everyos.bot.luwu.command.modules.moderation;

import everyos.bot.luwu.entity.Channel;
import everyos.bot.luwu.entity.Member;
import everyos.bot.luwu.entity.Server;
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
