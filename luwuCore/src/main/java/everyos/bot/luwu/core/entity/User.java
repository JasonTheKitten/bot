package everyos.bot.luwu.core.entity;

import everyos.bot.chat4j.ChatClient;
import everyos.bot.chat4j.entity.ChatUser;
import everyos.bot.chat4j.functionality.ChatInterface;
import everyos.bot.chat4j.functionality.ChatInterfaceProvider;
import reactor.core.publisher.Mono;

public class User implements ChatInterfaceProvider {
	private Connection connection;
	private ChatUser user;

	public User(Connection connection, ChatUser user) {
		this.connection = connection;
		this.user = user;
	}
	
	public Mono<Channel> getPrivateChannel() {
		return user.getPrivateChannel().map(channel->new Channel(connection, channel));
	}

	@Override public <T extends ChatInterface> boolean supportsInterface(Class<T> cls) {
		return user.supportsInterface(cls);
	}

	@Override public <T extends ChatInterface> T getInterface(Class<T> cls) {
		return user.getInterface(cls);
	}
	
	@Override public ChatClient getClient() {
		return user.getClient();
	}
	
	public Mono<Member> asMemberOf(Channel channel) {
		return channel.getMember(user.getID());
	}

	public long getID() {
		return user.getID();
	}

	public String getHumanReadableID() {
		return user.getHumanReadableID();
	}
}
