package everyos.bot.luwu.core.entity;

import java.util.Optional;

import everyos.bot.chat4j.entity.ChatMessage;
import reactor.core.publisher.Mono;

public class Message {
	private ChatMessage message;
	private Connection connection;

	public Message(Connection connection, ChatMessage message) {
		this.message = message;
		this.connection = connection;
	}
	
	public Optional<String> getContent() {
		return message.getContent();
	}

	public Mono<Void> delete() {
		return message.delete();
	}

	public Mono<Void> suppressEmbeds(boolean b) {
		return Mono.empty();
	}

	public Connection getConnection() {
		return connection;
	};
	public Client getClient() {
		return connection.getClient();
	}

	public Mono<Channel> getChannel() {
		return message.getChannel().map(channel->new Channel(connection, channel));
	}
}
