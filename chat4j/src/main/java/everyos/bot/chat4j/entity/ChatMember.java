package everyos.bot.chat4j.entity;

import reactor.core.publisher.Mono;

public interface ChatMember extends ChatUser {
	/**
	 * Returns if this member has less permission than the member being compared.
	 * @param chatMember The member being compared
	 * @return A mono that, when subscribed to, emits a boolean representing if this member has less permission
	 */
	public Mono<Boolean> isHigherThan(ChatMember chatMember);

	public Mono<ChatGuild> getServer();
}