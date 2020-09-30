package everyos.bot.luwu.entity;

import java.util.Optional;

import everyos.bot.chat4j.entity.ChatMessage;
import everyos.bot.luwu.database.Database;
import reactor.core.publisher.Mono;

public class Message {
	private ChatMessage message;

	public Message(ChatMessage message, Database database) {
		this.message = message;
	}
	
	public Optional<String> getContent() {
		return message.getContent();
	}

	public Mono<Void> delete() {
		return message.delete();
	};
}
