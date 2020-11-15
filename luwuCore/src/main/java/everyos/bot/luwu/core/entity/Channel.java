package everyos.bot.luwu.core.entity;

import java.util.function.Consumer;

import everyos.bot.chat4j.entity.ChatChannel;
import everyos.bot.chat4j.functionality.channel.ChatChannelTextInterface;
import everyos.bot.chat4j.functionality.message.MessageCreateSpec;
import everyos.bot.luwu.core.database.DBDocument;
import everyos.bot.luwu.core.functionality.Interface;
import everyos.bot.luwu.core.functionality.InterfaceProvider;
import everyos.bot.luwu.core.functionality.channel.ChannelTextInterface;
import reactor.core.publisher.Mono;

public class Channel implements InterfaceProvider {
	private Connection connection;
	private ChatChannel channel;
	private DBDocument document;

	public Channel(Connection connection, ChatChannel channel, DBDocument document) {
		this.connection = connection;
		this.channel = channel;
		this.document = document;
	}
	
	public <T extends Interface> boolean supportsInterface(Class<T> cls) {
		if (cls==ChannelTextInterface.class) {
			return true;
		}
		return false;
	};
	@SuppressWarnings("unchecked")
	public <T extends Interface> T getInterface(Class<T> cls) {
		if (cls==ChannelTextInterface.class) {
			return (T) new ChannelTextInterfaceImp(this);
		}
		return null;
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
	public String getType() {
		return document.getObject().getOrDefaultString(
			"type", connection.getBotEngine().getDefaultUserCaseName());
	}

	public ChannelID getID() {
		return new ChannelID(channel.getID());
	}
	
	public String getName() {
		return channel.getName();
	}

	public ChatChannel getRaw() {
		return channel;
	}

	public <T extends Channel> Mono<T> as(ChannelFactory<T> factory) {
		return factory.createChannel(connection, channel, getDocument());
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
	
	protected DBDocument getDocument() {
		return document;
	}
	
	public static Mono<Channel> getChannel(Connection connection, ChatChannel channel) {
		//TODO: Consider client id
		return connection.getClient().getBotEngine().getDatabase()
			.collection("channels")
			.scan()
			.with("cid", channel.getID())
			.orCreate(obj->{})
			
			.map(document->new Channel(connection, channel, document));
	}
	
	//TODO: Read+Edit
	
	
	private static class ChannelTextInterfaceImp implements ChannelTextInterface {
		private Channel channel;
		private ChatChannelTextInterface textGrip;

		public ChannelTextInterfaceImp(Channel channel) {
			this.channel = channel;
			this.textGrip = channel.channel.getInterface(ChatChannelTextInterface.class);
		}

		@Override public Connection getConnection() {
			return channel.connection;
		}

		@Override public Client getClient() {
			return channel.getClient();
		}

		@Override public Mono<Message> send(String text) {
			return textGrip.send(text).map(message->new Message(channel.connection, message));
		}

		@Override public Mono<Message> send(Consumer<MessageCreateSpec> spec) {
			return textGrip.send(spec).map(message->new Message(channel.connection, message));
		}
	}
}
