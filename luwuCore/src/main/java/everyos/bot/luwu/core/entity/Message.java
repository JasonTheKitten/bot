package everyos.bot.luwu.core.entity;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.WeakHashMap;

import everyos.bot.chat4j.entity.ChatMessage;
import everyos.bot.chat4j.functionality.message.ChatMessageReactionInterface;
import everyos.bot.luwu.core.database.DBDocument;
import everyos.bot.luwu.core.functionality.Interface;
import everyos.bot.luwu.core.functionality.InterfaceProvider;
import reactor.core.publisher.Mono;

public class Message implements InterfaceProvider {
	private ChatMessage message;
	private Connection connection;
	private Map<String, DBDocument> documents;

	protected Message(Connection connection, ChatMessage message, Map<String, DBDocument> documents) {
		this.message = message;
		this.connection = connection;
		this.documents = documents==null?
			Collections.synchronizedMap(new WeakHashMap<>()):
			documents;
	}
	public Message(Connection connection, ChatMessage message) {
		this(connection, message, null);
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

	public Mono<Void> addReaction(EmojiID reaction) {
		// TODO: Move this to a feature
		if (reaction.getID().isPresent()) {
			return message.getInterface(ChatMessageReactionInterface.class)
				.addReaction(reaction.getID().get());
		} else if (reaction.getName().isPresent()) {
			return message.getInterface(ChatMessageReactionInterface.class)
				.addReaction(reaction.getName().get());
		}
		return Mono.empty();
		
	}
	public Mono<Void> removeReaction(EmojiID reaction) {
		// TODO: Move this to a feature
		if (reaction.getID().isPresent()) {
			return message.getInterface(ChatMessageReactionInterface.class)
				.removeReaction(reaction.getID().get());
		} else if (reaction.getName().isPresent()) {
			return message.getInterface(ChatMessageReactionInterface.class)
				.removeReaction(reaction.getName().get());
		}
		return Mono.empty();
	}

	public ChannelID getChannelID() {
		return new ChannelID(connection, message.getChannelID(), this.connection.getClient().getID());
	}
	
	public UserID getAuthorID() {
		return new UserID(connection, message.getAuthorID());
	}
	
	public Mono<User> getAuthor() {
		return message.getAuthor().map(user->new User(connection, user));
	}
	
	public Mono<Void> pin() {
		return message.pin();
	}
	
	protected Map<String, DBDocument> getDocuments() {
		return documents;
	}
	
	protected Mono<DBDocument> getNamedDocument(String name) {
		if (documents.containsKey(name)) {
			return Mono.just(documents.get(name));
		}
		return getConnection().getBotEngine().getDatabase()
			.collection(name).scan()
			.with("mid", message.getID())
			.with("cid", message.getAuthorID())
			.orCreate(document->{})
			.doOnNext(document->documents.put(name, document));
	}
	
	protected Mono<DBDocument> getGlobalDocument() {
		return getNamedDocument("messages");
	}

	@Override public <T extends Interface> boolean supportsInterface(Class<T> cls) {
		return false;
	}

	@Override public <T extends Interface> T getInterface(Class<T> cls) {
		return null;
	}
	public <T extends Message> Mono<T> as(MessageFactory<T> factory) {
		return factory.createMessage(connection, message, getDocuments());
	}
}
