package everyos.bot.luwu.core.entity;

import everyos.bot.chat4j.entity.ChatChannel;
import everyos.bot.chat4j.functionality.ChatInterface;
import everyos.bot.luwu.core.database.DBDocument;
import reactor.core.publisher.Mono;

public class Channel {
	private Connection connection;
	private ChatChannel channel;

	public Channel(Connection connection, ChatChannel channel) {
		this.connection = connection;
		this.channel = channel;
	}
	
	public <T extends ChatInterface> boolean supportsInterface(Class<T> cls) {
		return channel.supportsInterface(cls);
	};
	public <T extends ChatInterface> T getInterface(Class<T> cls) {
		return channel.getInterface(cls);
	};
	
	public Mono<Member> getMember(long uid) {
		return channel.getConnection().getUserByID(uid)
			.flatMap(user->user.asMemberOf(channel))
			.map(member->new Member(connection, member));
	}

	//TODO: Perhaps add DB Access to a .read
	public Mono<String[]> getPrefixes() {
		return Mono.just(new String[] {
			//TODO: Query DB. Also, prefixes might belong to guild, based on model
			//This would be better handled as a interface?
			"luwu ",
			"LUWU ",
			"Luwu"
		});
	}
	public Mono<String> getType() {
		return getDocument()
			.map(document->document.getObject().getOrDefaultString(
				"type", connection.getBotEngine().getDefaultUserCaseName()));
	}

	public ChannelID getID() {
		return new ChannelID() {
			@Override public long getLong() {
				return channel.getID();
			}
		};
	}

	public ChatChannel getRaw() {
		return channel;
	}

	public <T extends Channel> T as(ChannelFactory<T> factory) {
		return factory.createChannel(connection, channel);
	}

	public Mono<Server> getServer() {
		return channel.getGuild().map(guild->new Server(guild));
	}

	public boolean isPrivateChannel() {
		return false;
	}
	
	public Client getClient() {
		return connection.getClient();
	}
	
	protected Mono<DBDocument> getDocument() {
		//TODO: Consider client id
		return this.getClient().getBotEngine().getDatabase()
			.collection("channels")
			.scan()
			.with("cid", getID().getLong())
			.orCreate(obj->{});
	}
	
	//TODO: Read+Edit
	
}
