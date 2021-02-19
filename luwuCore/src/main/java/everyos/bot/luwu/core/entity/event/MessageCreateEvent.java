package everyos.bot.luwu.core.entity.event;

import java.util.Optional;

import everyos.bot.chat4j.event.ChatMessageCreateEvent;
import everyos.bot.luwu.core.entity.Client;
import everyos.bot.luwu.core.entity.Connection;
import everyos.bot.luwu.core.entity.Member;
import everyos.bot.luwu.core.entity.Message;
import everyos.bot.luwu.core.entity.User;
import reactor.core.publisher.Mono;

public class MessageCreateEvent extends MessageEvent {
	private Connection connection;
	private ChatMessageCreateEvent event;
	public MessageCreateEvent(Connection connection, ChatMessageCreateEvent event) {
		super(connection, event);
		this.connection = connection;
		this.event = event;
	}
	public Mono<Message> getMessage() {
		return event.getMessage().map(message->new Message(connection, message));
	}
	public Mono<User> getSender() {
		return event.getSender().map(user->new User(connection, user));
	}
	public Mono<Optional<Member>> getSenderAsMember() {
		return event.getSenderAsMember().map(membero->
			membero.map(member->
				new Member(connection, member)));
	}
	public Connection getConnection() {
		return connection;
	}
	public Client getClient() {
		return connection.getClient();
	}
}
