package everyos.bot.luwu.core.functionality.message;

import everyos.bot.luwu.core.entity.EmojiID;
import everyos.bot.luwu.core.entity.User;
import everyos.bot.luwu.core.functionality.Interface;
import reactor.core.publisher.Mono;

public interface MessageReactionInterface extends Interface {
	Mono<Void> addReaction(EmojiID reaction);
	Mono<Void> removeReaction(EmojiID reaction);
	Mono<User[]> getReactors(EmojiID reaction);
}
