package everyos.bot.luwu.core.entity.event;

import java.util.Optional;

import everyos.bot.chat4j.event.ChatMessageEditEvent;
import everyos.bot.luwu.core.entity.Connection;
import everyos.bot.luwu.core.entity.Message;

public class MessageEditEvent extends MessageEvent {
	private ChatMessageEditEvent messageEditEvent;

	public MessageEditEvent(Connection connection, ChatMessageEditEvent messageEditEvent) {
		super(connection, messageEditEvent);
		
		this.messageEditEvent = messageEditEvent;
	}
	
	public Optional<Message> getOldMessage() {
		return messageEditEvent.getOldMessage()
			.map(message->new Message(getConnection(), message));
	}
}
