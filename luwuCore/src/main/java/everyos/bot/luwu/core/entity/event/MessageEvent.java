package everyos.bot.luwu.core.entity.event;

import everyos.bot.chat4j.event.ChatMessageEvent;
import everyos.bot.luwu.core.entity.Connection;
import everyos.bot.luwu.core.entity.Message;
import everyos.bot.luwu.core.entity.Server;
import reactor.core.publisher.Mono;

public class MessageEvent extends Event {
	private Connection connection;
	private ChatMessageEvent messageEvent;
	private Message cachedMessage;

	public MessageEvent(Connection connection, ChatMessageEvent messageEvent) {
		super(connection, messageEvent);
		this.connection = connection;
		this.messageEvent = messageEvent;
	}
	
	public Mono<Message> getMessage() {
		return
			Mono.justOrEmpty(cachedMessage)
			.switchIfEmpty(messageEvent.getMessage().map(message->new Message(connection, message))
				.doOnNext(m->cachedMessage=m));
	}

	public Mono<Server> getServer() {
		return messageEvent.getGuild()
			.map(server->new Server(connection, server));
	};
}
