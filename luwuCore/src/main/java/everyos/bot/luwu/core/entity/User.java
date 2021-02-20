package everyos.bot.luwu.core.entity;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.WeakHashMap;

import everyos.bot.chat4j.entity.ChatUser;
import everyos.bot.luwu.core.database.DBDocument;
import everyos.bot.luwu.core.functionality.Interface;
import everyos.bot.luwu.core.functionality.InterfaceProvider;
import reactor.core.publisher.Mono;

public class User implements InterfaceProvider {
	private Connection connection;
	private ChatUser user;
	private Map<String, DBDocument> documents;

	//TODO: Make constructor private, for cache sake
	public User(Connection connection, ChatUser user) {
		this(connection, user, null);
	}
	
	protected User(Connection connection, ChatUser user, Map<String, DBDocument> documents) {
		this.connection = connection;
		this.user = user;
		this.documents = documents==null?
			Collections.synchronizedMap(new WeakHashMap<>()):
			documents;
	}

	public Mono<Channel> getPrivateChannel() {
		return user.getPrivateChannel().map(channel->new Channel(connection, channel));
	}

	@Override public <T extends Interface> boolean supportsInterface(Class<T> cls) {
		//return user.supportsInterface(cls);
		return false;
	}

	@Override public <T extends Interface> T getInterface(Class<T> cls) {
		//return user.getInterface(cls);
		return null;
	}
	
	@Override public Client getClient() {
		return connection.getClient();
	}
	
	public Mono<Member> asMemberOf(Channel channel) {
		return channel.getMember(getID());
	}

	public UserID getID() {
		return new UserID(connection, user.getID());
	}

	public String getHumanReadableID() {
		return user.getHumanReadableID();
	}

	public boolean isBot() {
		return user.isBot();
	}
	
	public Optional<String> getAvatarUrl() {
		return user.getAvatarURL();
	}
	
	@Override
	public Connection getConnection() {
		return connection;
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
			.with("uid", user.getID())
			.orCreate(document->{})
			.doOnNext(document->documents.put(name, document));
	}
	
	protected Mono<DBDocument> getGlobalDocument() {
		return getNamedDocument("users");
	}
	
	@Override
	public String toString() {
		return String.valueOf(getID().getLong());
	}
}
