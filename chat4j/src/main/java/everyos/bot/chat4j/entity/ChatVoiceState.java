package everyos.bot.chat4j.entity;

import reactor.core.publisher.Mono;

public interface ChatVoiceState {
	Mono<ChatChannel> getChannel();
}
