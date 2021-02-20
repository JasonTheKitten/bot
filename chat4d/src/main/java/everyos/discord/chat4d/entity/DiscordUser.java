package everyos.discord.chat4d.entity;

import java.util.Optional;

import discord4j.core.object.entity.User;
import discord4j.rest.util.Image.Format;
import everyos.bot.chat4j.ChatClient;
import everyos.bot.chat4j.ChatConnection;
import everyos.bot.chat4j.entity.ChatChannel;
import everyos.bot.chat4j.entity.ChatMember;
import everyos.bot.chat4j.entity.ChatUser;
import everyos.bot.chat4j.functionality.ChatInterface;
import reactor.core.publisher.Mono;

public class DiscordUser implements ChatUser {
	private User user;
	private ChatConnection connection;

	public DiscordUser(ChatConnection connection, User user) {
		this.user = user;
		this.connection = connection;
	}

	@Override
	public <T extends ChatInterface> boolean supportsInterface(Class<T> cls) {
		return false;
	}

	@Override
	public <T extends ChatInterface> T getInterface(Class<T> cls) {
		return null;
	}

	@Override
	public Mono<ChatMember> asMemberOf(ChatChannel channel) {
		return DiscordMember.instatiate(getConnection(), user, channel);
	}

	@Override
	public long getID() {
		return user.getId().asLong();
	}
	
	@Override
	public ChatClient getClient() {
		return getConnection().getClient();
	}
	@Override
	public ChatConnection getConnection() {
		return connection;
	}

	@Override
	public String getHumanReadableID() {
		return user.getUsername()+"#"+user.getDiscriminator();
	}

	@Override
	public Mono<ChatChannel> getPrivateChannel() {
		return user.getPrivateChannel().map(channel->new DiscordChannel(connection, channel));
	}

	@Override
	public boolean isBot() {
		return user.isBot();
	}
	
	@Override
	public Optional<String> getAvatarURL() {
		return user.getAvatarUrl(Format.PNG);
	}
}
