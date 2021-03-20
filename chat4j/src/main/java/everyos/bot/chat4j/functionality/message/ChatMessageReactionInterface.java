package everyos.bot.chat4j.functionality.message;

import everyos.bot.chat4j.entity.ChatUser;
import everyos.bot.chat4j.functionality.ChatInterface;
import reactor.core.publisher.Mono;

public interface ChatMessageReactionInterface extends ChatInterface {
	public Mono<Void> addReaction(String name);
	public Mono<Void> addReaction(long id);
	public Mono<Void> removeReaction(String name);
	public Mono<Void> removeReaction(long id);
	Mono<ChatUser[]> getReactions(String name);
	Mono<ChatUser[]> getReactions(long id);
}
