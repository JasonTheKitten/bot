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
		return message.getChannel().map(channel->new Channel(connection, channel));
	}

	public Mono<Void> addReaction(String string) {
		// TODO: Auto-generated method stub
		// TODO: Also, move this to a feature
		return Mono.empty();
	}

	public ChannelID getChannelID() {
		// TODO Auto-generated method stub
		return new ChannelID() {
			@Override public long getLong() {
				return message.getChannelID();
			}
		};
	}

	@Override public <T extends Interface> boolean supportsInterface(Class<T> cls) {
		return false;
	}

	@Override public <T extends Interface> T getInterface(Class<T> cls) {
		return null;
	}
}
