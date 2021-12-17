package everyos.bot.luwu.core.entity;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.WeakHashMap;
import java.util.function.Consumer;

import everyos.bot.chat4j.entity.ChatAttachment;
import everyos.bot.chat4j.entity.ChatMessage;
import everyos.bot.chat4j.functionality.message.ChatMessageReactionInterface;
import everyos.bot.chat4j.functionality.message.MessageEditSpec;
import everyos.bot.luwu.core.database.DBDocument;
import everyos.bot.luwu.core.entity.imp.MessageReactionInterfaceImp;
import everyos.bot.luwu.core.functionality.Interface;
import everyos.bot.luwu.core.functionality.InterfaceProvider;
import everyos.bot.luwu.core.functionality.message.MessageReactionInterface;
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
	
	public Mono<Message> edit(Consumer<MessageEditSpec> spec) {
		return message.edit(spec)
			.map(message->new Message(connection, message));
	};

	public ChannelID getChannelID() {
		return new ChannelID(connection, message.getChannelID(), this.connection.getClient().getID());
	}
	
	public UserID getAuthorID() {
		return new UserID(connection, message.getAuthorID());
	}
	
	public MessageID getMessageID() {
		return new MessageID(getChannelID(), message.getID());
	}
	
	public Mono<User> getAuthor() {
		return message.getAuthor().map(user->new User(connection, user));
	}
	
	public Mono<Void> pin() {
		return message.pin();
	}
	
	public ChatAttachment[] getAttachments() {
		return message.getAttachments();
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

	@Override
	public <T extends Interface> boolean supportsInterface(Class<T> cls) {
		if (cls == MessageReactionInterface.class) {
			return true;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Interface> T getInterface(Class<T> cls) {
		if (cls == MessageReactionInterface.class) {
			return (T) new MessageReactionInterfaceImp(connection,
				message.getInterface(ChatMessageReactionInterface.class));
		}
		return null;
	}
	
	public <T extends Message> Mono<T> as(MessageFactory<T> factory) {
		return factory.createMessage(connection, message, getDocuments());
	}
	
}
