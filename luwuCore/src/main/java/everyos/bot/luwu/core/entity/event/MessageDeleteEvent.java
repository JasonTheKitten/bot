package everyos.bot.luwu.core.entity.event;

import everyos.bot.chat4j.event.ChatMessageEvent;
import everyos.bot.luwu.core.entity.Connection;

public class MessageDeleteEvent extends MessageEvent {
	public MessageDeleteEvent(Connection connection, ChatMessageEvent messageEvent) {
		super(connection, messageEvent);
	}
}
