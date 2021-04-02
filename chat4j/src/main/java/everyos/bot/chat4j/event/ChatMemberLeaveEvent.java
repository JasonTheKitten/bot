package everyos.bot.chat4j.event;

import everyos.bot.chat4j.entity.ChatGuild;
import everyos.bot.chat4j.entity.ChatMember;
import reactor.core.publisher.Mono;

public interface ChatMemberLeaveEvent extends ChatMemberEvent {
	ChatMember getMember();
	Mono<ChatGuild> getGuild();
}
