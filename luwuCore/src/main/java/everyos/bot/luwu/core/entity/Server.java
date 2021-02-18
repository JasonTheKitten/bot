package everyos.bot.luwu.core.entity;

import java.util.HashMap;
import java.util.Map;

import everyos.bot.chat4j.entity.ChatGuild;
import everyos.bot.luwu.core.database.DBDocument;
import everyos.bot.luwu.core.functionality.Interface;
import everyos.bot.luwu.core.functionality.InterfaceProvider;
import reactor.core.publisher.Mono;

public class Server implements InterfaceProvider {
	private Connection connection;
	private ChatGuild guild;
	private Map<String, DBDocument> documents;

	public Server(Connection connection, ChatGuild guild) {
		this(connection, guild, null);
	}

	public Server(Connection connection, ChatGuild guild, Map<String, DBDocument> documents) {
		this.connection = connection;
		this.guild = guild;
		this.documents = new HashMap<>();
	}

	public ServerID getID() {
		return new ServerID(connection, guild.getID()); 
	}

	@Override public <T extends Interface> boolean supportsInterface(Class<T> cls) {
		return false;
	}

	@Override public <T extends Interface> T getInterface(Class<T> cls) {
		return null;
	}

	@Override public Client getClient() {
		return connection.getClient();
	}
	
	@Override public Connection getConnection() {
		return connection;
	}

	public String getName() {
		return guild.getName();
	}
	
	public <T extends Server> T getWithExtension(ServerFactory<T> factory) {
		return factory.create(connection, guild, documents);
	}
	
	protected ChatGuild getGuild() {
		return guild;
	}
	
	protected Mono<DBDocument> getNamedDocument(String name) {
		if (documents.containsKey(name)) {
			return Mono.just(documents.get(name));
		}
		return getConnection().getBotEngine().getDatabase()
			.collection(name).scan()
			.with("gid", guild.getID())
			.orCreate(document->{})
			.doOnNext(document->documents.put(name, document));
	}
	
	protected Mono<DBDocument> getServerDocument() {
		return getNamedDocument("servers");
	}
}
