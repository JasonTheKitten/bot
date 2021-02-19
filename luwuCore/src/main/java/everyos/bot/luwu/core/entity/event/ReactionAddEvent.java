package everyos.bot.luwu.core.entity.event;

import everyos.bot.chat4j.event.ChatReactionAddEvent;
import everyos.bot.luwu.core.entity.Connection;

public class ReactionAddEvent extends ReactionEvent {
	public ReactionAddEvent(Connection connection, ChatReactionAddEvent reactionEvent) {
		super(connection, reactionEvent);
	}
}
