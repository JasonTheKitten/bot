package everyos.nertivia.chat4n.entity;

import everyos.bot.chat4j.ChatClient;
import everyos.bot.chat4j.ChatConnection;
import everyos.bot.chat4j.entity.ChatChannel;
import everyos.bot.chat4j.entity.ChatMember;
import everyos.bot.chat4j.entity.ChatUser;
import everyos.bot.chat4j.functionality.ChatInterface;
import everyos.nertivia.nertivia4j.entity.User;
import reactor.core.publisher.Mono;

public class NertiviaUser implements ChatUser {
	private ChatConnection connection;
	private User user;

	public NertiviaUser(ChatConnection connection, User author) {
		this.user = author;
		this.connection = connection;
	}

	@Override public <T extends ChatInterface> boolean supportsInterface(Class<T> cls) {
		return false;
	}

	@Override public <T extends ChatInterface> T getInterface(Class<T> cls) {
		return null;
	}

	@Override public Mono<ChatMember> asMemberOf(ChatChannel channel) {
		return NertiviaMember.instatiate(getConnection(), user, channel).cast(ChatMember.class);
	}

	@Override public long getID() {
		return user.getID();
	}

	@Override public ChatClient getClient() {
		return getConnection().getClient();
	}
	@Override public ChatConnection getConnection() {
		return connection;
	}

	@Override public String getHumanReadableID() {
		return "<TODO>";
	}

	@Override public Mono<ChatChannel> getPrivateChannel() {
		return null;
	}
}
