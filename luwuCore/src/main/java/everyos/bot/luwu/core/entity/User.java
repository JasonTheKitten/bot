package everyos.bot.luwu.core.entity;

import everyos.bot.chat4j.entity.ChatUser;
import everyos.bot.luwu.core.functionality.Interface;
import everyos.bot.luwu.core.functionality.InterfaceProvider;
import reactor.core.publisher.Mono;

public class User implements InterfaceProvider {
	private Connection connection;
	private ChatUser user;

	public User(Connection connection, ChatUser user) {
		this.connection = connection;
		this.user = user;
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
		return channel.getMember(user.getID());
	}

	public UserID getID() {
		return new UserID() {
			@Override public long getLong() {
				return user.getID();
			}
		};
	}

	public String getHumanReadableID() {
		return user.getHumanReadableID();
	}

	public boolean isBot() {
		return user.isBot();
	}
}
