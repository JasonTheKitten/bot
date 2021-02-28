package everyos.bot.luwu.core.entity;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

import everyos.bot.chat4j.entity.ChatChannel;
import everyos.bot.chat4j.functionality.channel.ChatChannelVoiceInterface;
import everyos.bot.luwu.core.database.DBDocument;
import everyos.bot.luwu.core.entity.imp.ChannelTextInterfaceImp;
import everyos.bot.luwu.core.entity.imp.ChannelVoiceInterfaceImp;
import everyos.bot.luwu.core.functionality.Interface;
import everyos.bot.luwu.core.functionality.InterfaceProvider;
import everyos.bot.luwu.core.functionality.channel.ChannelTextInterface;
import everyos.bot.luwu.core.functionality.channel.ChannelVoiceInterface;
import reactor.core.publisher.Mono;

public class Channel implements InterfaceProvider {
	private Connection connection;
	private ChatChannel channel;
	private Map<String, DBDocument> documents;

	protected Channel(Connection connection, ChatChannel channel, Map<String, DBDocument> documents) {
		this.connection = connection;
		this.channel = channel;
		this.documents = documents==null?
			Collections.synchronizedMap(new WeakHashMap<>()):
			documents;
	}
	
	public Channel(Connection connection, ChatChannel channel) {
		this(connection, channel, null);
	}

	public <T extends Interface> boolean supportsInterface(Class<T> cls) {
		return
			(cls==ChannelTextInterface.class) &&
			(cls==ChannelVoiceInterface.class&&channel.supportsInterface(ChatChannelVoiceInterface.class));
	};
	@SuppressWarnings("unchecked")
	public <T extends Interface> T getInterface(Class<T> cls) {
		if (cls==ChannelTextInterface.class) {
			return (T) new ChannelTextInterfaceImp(this);
		}
		if (cls==ChannelVoiceInterface.class) {
			return (T) new ChannelVoiceInterfaceImp(this);
		}
		return null;
	};
	
	public Mono<Member> getMember(UserID uid) {
		return channel.getConnection().getUserByID(uid.getLong())
			.flatMap(user->user.asMemberOf(channel))
			.map(member->new Member(connection, member));
	}

	//TODO: Perhaps add DB Access to a .read
	public Mono<String[]> getPrefixes() {
		return Mono.just(new String[] {
			//TODO: Query DB. Also, prefixes might belong to guild, based on model
			//This would be better handled as a interface?
			//TODO: Perhaps read this from JSON?
			"luwu ",
			"LUWU ",
			"Luwu"
		});
	}
	public Mono<String> getType() {
		return getGlobalDocument()
			.map(document->document.getObject().getOrDefaultString(
				"type", connection.getBotEngine().getDefaultUserCaseName()));
	}

	public ChannelID getID() {
		return new ChannelID(connection, channel.getID(), getClient().getID());
	}
	
	public String getName() {
		return channel.getName();
	}

	public ChatChannel getRaw() {
		return channel;
	}

	public <T extends Channel> Mono<T> as(ChannelFactory<T> factory) {
		return factory.createChannel(connection, channel, getDocuments());
	}

	public Mono<Server> getServer() {
		return channel.getGuild()
			.map(guild->new Server(connection, guild));
	}

	public boolean isPrivateChannel() {
		return channel.isPrivate();
	}
	

	public Mono<Void> reset() {
		return getGlobalDocument().flatMap(doc->doc.delete());
	}
	
	public Connection getConnection() {
		return connection;
	}
	
	public Client getClient() {
		return connection.getClient();
	}
	
	protected Map<String, DBDocument> getDocuments() {
		return documents;
	}
	
	protected Mono<DBDocument> getNamedDocument(String name) {
		if (documents.containsKey(name)) {
			return Mono.just(documents.get(name));
		}
		//TODO: Consider client id
		return getConnection().getBotEngine().getDatabase()
			.collection(name).scan()
			.with("cid", channel.getID())
			.with("cliid", getClient().getID())
			.orCreate(document->{})
			.doOnNext(document->documents.put(name, document));
	}
	
	protected Mono<DBDocument> getGlobalDocument() {
		return getNamedDocument("channels");
	}
	
	public Mono<Message> getMessageByID(MessageID messageID) {
		return channel.getMessageByID(messageID.getLong())
			.map(message->new Message(connection, message));
	}
	
	//TODO: Read+Edit
}
