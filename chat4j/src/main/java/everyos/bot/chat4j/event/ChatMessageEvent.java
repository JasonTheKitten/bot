package everyos.bot.chat4j.event;

import everyos.bot.chat4j.entity.ChatMessage;
import reactor.core.publisher.Mono;

public interface ChatMessageEvent extends ChatEvent {
	public Mono<ChatMessage> getMessage();
}
