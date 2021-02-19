package everyos.bot.luwu.core.entity.event;

import everyos.bot.chat4j.event.ChatReactionRemoveEvent;
import everyos.bot.luwu.core.entity.Connection;

public class ReactionRemoveEvent extends ReactionEvent {
	public ReactionRemoveEvent(Connection connection, ChatReactionRemoveEvent reactionEvent) {
		super(connection, reactionEvent);
	}
}
