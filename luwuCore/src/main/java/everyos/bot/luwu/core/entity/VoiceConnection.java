package everyos.bot.luwu.core.entity;

import reactor.core.publisher.Mono;

public interface VoiceConnection {
	Mono<Void> leave();
}
