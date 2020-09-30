package everyos.bot.luwu.entity;

import everyos.bot.chat4j.ChatClient;
import everyos.bot.chat4j.entity.ChatUser;
import everyos.bot.chat4j.functionality.ChatInterface;
import everyos.bot.chat4j.functionality.ChatInterfaceProvider;
import everyos.bot.luwu.database.Database;
import reactor.core.publisher.Mono;

public class User implements ChatInterfaceProvider {
	private ChatUser user;
	private Database database;

	public User(ChatUser user, Database database) {
		this.user = user;
		this.database = database;
	}
	
	public Mono<Channel> getPrivateChannel() {
		return user.getPrivateChannel().map(channel->new Channel(channel, database));
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
