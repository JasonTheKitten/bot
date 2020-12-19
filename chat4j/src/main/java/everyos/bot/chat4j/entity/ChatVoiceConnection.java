package everyos.bot.chat4j.entity;

import reactor.core.publisher.Mono;

public interface ChatVoiceConnection {
	public Mono<Void> disconnect();
}
