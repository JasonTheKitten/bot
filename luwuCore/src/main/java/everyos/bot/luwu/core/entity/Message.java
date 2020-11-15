package everyos.bot.luwu.core.entity;

import java.util.Optional;

import everyos.bot.chat4j.entity.ChatMessage;
import everyos.bot.luwu.core.functionality.Interface;
import everyos.bot.luwu.core.functionality.InterfaceProvider;
import reactor.core.publisher.Mono;

public class Message implements InterfaceProvider {
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
		return message.getChannel().flatMap(channel->Channel.getChannel(connection, channel));
	}

	public Mono<Void> addReaction(String string) {
		// TODO: Auto-generated method stub
		// TODO: Also, move this to a feature
		return Mono.empty();
	}
	public Mono<Void> removeReaction(String string) {
		return Mono.empty();
	}

	public ChannelID getChannelID() {
		return new ChannelID(message.getChannelID());
	}
	
	public UserID getAuthorID() {
		return new UserID() {
			@Override public long getLong() {
				return message.getAuthorID();
			}
		};
	}
	
	public Mono<User> getAuthor() {
		return message.getAuthor().map(user->new User(connection, user));
	}
	
	public Mono<Void> pin() {
		return message.pin();
	}

	@Override public <T extends Interface> boolean supportsInterface(Class<T> cls) {
		return false;
	}

	@Override public <T extends Interface> T getInterface(Class<T> cls) {
		return null;
	}
}
