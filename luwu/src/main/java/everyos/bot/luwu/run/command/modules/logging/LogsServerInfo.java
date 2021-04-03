package everyos.bot.luwu.run.command.modules.logging;

import everyos.bot.luwu.core.entity.Channel;
import reactor.core.publisher.Mono;

public interface LogsServerInfo {
	Mono<Channel> getLogChannel();
}
