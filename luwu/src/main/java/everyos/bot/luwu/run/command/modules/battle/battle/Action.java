package everyos.bot.luwu.run.command.modules.battle.battle;

import reactor.core.publisher.Mono;

public interface Action {
	Mono<Void> execute();
}
