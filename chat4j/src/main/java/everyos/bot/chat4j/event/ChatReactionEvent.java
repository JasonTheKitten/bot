package everyos.bot.chat4j.event;

import java.util.Optional;

import everyos.bot.chat4j.entity.ChatMember;
import everyos.bot.chat4j.entity.ChatUser;
import reactor.core.publisher.Mono;

public interface ChatReactionEvent extends ChatMessageEvent {
	public Optional<String> getReactionString();
	public Optional<Long> getReactionLong();
	public Mono<ChatUser> getAuthor();
	public Mono<ChatMember> getAuthorAsMember();
}
