package everyos.bot.luwu.core.entity.event;

import everyos.bot.chat4j.event.ChatReactionEvent;
import everyos.bot.luwu.core.entity.Connection;
import everyos.bot.luwu.core.entity.EmojiID;

public class ReactionEvent extends MessageEvent {
	private ChatReactionEvent reactionEvent;
	
	public ReactionEvent(Connection connection, ChatReactionEvent reactionEvent) {
		super(connection, reactionEvent);
		this.reactionEvent = reactionEvent;
	}
	
	public EmojiID getReaction() {
		if (reactionEvent.getReactionLong().isPresent()) {
			return EmojiID.of(reactionEvent.getReactionLong().get());
		} else {
			return EmojiID.of(reactionEvent.getReactionString().get());
		}
	}
}
