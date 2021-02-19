package everyos.bot.luwu.core.entity.event;

import everyos.bot.chat4j.event.ChatMessageEvent;
import everyos.bot.luwu.core.entity.Connection;
import everyos.bot.luwu.core.entity.Message;
import reactor.core.publisher.Mono;

public class MessageEvent extends Event {
	private Connection connection;
	private ChatMessageEvent messageEvent;

	public MessageEvent(Connection connection, ChatMessageEvent messageEvent) {
		super(connection);
		this.connection = connection;
		this.messageEvent = messageEvent;
	}
	
	public Mono<Message> getMessage() {
		return messageEvent.getMessage().map(message->new Message(connection, message));
	};
}
