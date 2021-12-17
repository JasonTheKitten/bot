package everyos.bot.chat4j.entity;

import java.util.Optional;

import reactor.core.publisher.Mono;

public interface ChatMember extends ChatUser {
	/**
	 * Returns if this member has less permission than the member being compared.
	 * @param chatMember The member being compared
	 * @return A mono that, when subscribed to, emits a boolean representing if this member has less permission
	 */
	Mono<Boolean> isHigherThan(ChatMember chatMember);

	Mono<ChatGuild> getServer();

	Mono<Integer> getPermissions();

	Optional<String> getNickname();

	Optional<Long> getJoinTime();
}