package everyos.bot.chat4j.event;

import everyos.bot.chat4j.entity.ChatGuild;
import reactor.core.publisher.Mono;

public interface ChatServerDeleteEvent extends ChatServerEvent {
	Mono<ChatGuild> getGuild();
}