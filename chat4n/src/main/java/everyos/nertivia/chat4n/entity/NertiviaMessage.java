package everyos.nertivia.chat4n.entity;

import java.util.Optional;

import everyos.bot.chat4j.ChatClient;
import everyos.bot.chat4j.ChatConnection;
import everyos.bot.chat4j.entity.ChatChannel;
import everyos.bot.chat4j.entity.ChatMember;
import everyos.bot.chat4j.entity.ChatMessage;
import everyos.bot.chat4j.entity.ChatUser;
import everyos.bot.chat4j.functionality.ChatInterface;
import everyos.bot.chat4j.functionality.message.ChatMessageReactionInterface;
import everyos.nertivia.nertivia4j.entity.Message;
import reactor.core.publisher.Mono;

public class NertiviaMessage implements ChatMessage {
	private Message message;
	private ChatConnection connection;

	public NertiviaMessage(ChatConnection connection, Message message) {
		this.message = message;
		this.connection = connection;
	}

	@Override
	public Optional<String> getContent() {
		return message.getContent();
	}

	@Override
	public Mono<Void> delete() {
		return message.delete();
	}

	@Override
	public Mono<ChatChannel> getChannel() {
		return message.getChannel().map(channel->new NertiviaChannel(getConnection(), channel));
	}

	@Override
	public Mono<ChatUser> getAuthor() {
		return message.getAuthor().map(user->new NertiviaUser(connection, user));
	}

	@Override
	public Mono<ChatMember> getAuthorAsMember() {
		// TODO Auto-generated method stub
		return Mono.empty();
	}

	@Override
	public long getTimestamp() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public <T extends ChatInterface> boolean supportsInterface(Class<T> cls) {
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends ChatInterface> T getInterface(Class<T> cls) {
		if (cls==ChatMessageReactionInterface.class) {
			return (T) new ChatMessageReactionInterface() {

				@Override
				public ChatConnection getConnection() {
					return connection;
				}

				@Override
				public ChatClient getClient() {
					return connection.getClient();
				}

				@Override
				public Mono<Void> addReaction(String name) {
					return Mono.empty();
				}

				@Override
				public Mono<Void> addReaction(long id) {
					return Mono.empty();
				}

				@Override
				public Mono<Void> removeReaction(String name) {
					return Mono.empty();
				}

				@Override
				public Mono<Void> removeReaction(long id) {
					return Mono.empty();
				}
				
			};
		}
		return null;
	}
	
	@Override
	public ChatClient getClient() {
		return getConnection().getClient();
	}

	@Override
	public ChatConnection getConnection() {
		return connection;
	}

	@Override
	public Mono<Void> pin() {
		return Mono.empty();
	}

	@Override public long getChannelID() {
		return message.getChannelID();
	}

	@Override public long getAuthorID() {
		return message.getAuthorID();
	}

	@Override
	public long getID() {
		return message.getID();
	}
}
