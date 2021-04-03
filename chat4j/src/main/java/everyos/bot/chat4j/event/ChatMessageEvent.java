package everyos.bot.chat4j.event;

import everyos.bot.chat4j.entity.ChatGuild;
import everyos.bot.chat4j.entity.ChatMessage;
import reactor.core.publisher.Mono;

public interface ChatMessageEvent extends ChatEvent {
	Mono<ChatMessage> getMessage();
	Mono<ChatGuild> getGuild();
}
