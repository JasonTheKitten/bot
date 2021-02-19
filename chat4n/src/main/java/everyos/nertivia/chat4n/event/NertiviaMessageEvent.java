package everyos.nertivia.chat4n.event;

import everyos.bot.chat4j.ChatConnection;
import everyos.bot.chat4j.entity.ChatMessage;
import everyos.bot.chat4j.event.ChatMessageEvent;
import reactor.core.publisher.Mono;

public abstract class NertiviaMessageEvent extends NertiviaEvent implements ChatMessageEvent {
	public NertiviaMessageEvent(ChatConnection connection) {
		super(connection);
	}

	@Override
	public Mono<ChatMessage> getMessage() {
		return null;
	}
}
