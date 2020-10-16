package everyos.bot.luwu.core.entity;

import everyos.bot.chat4j.entity.ChatChannel;
import everyos.bot.chat4j.functionality.ChatInterface;
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
			"luwu "
		});
	}
	public Mono<String> getType() {
		return null;
	}

	public ChannelID getID() {
		return null;
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
	
	//TODO: Read+Edit
	
}
