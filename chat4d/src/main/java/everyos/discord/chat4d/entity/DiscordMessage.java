package everyos.discord.chat4d.entity;

import java.util.Optional;

import discord4j.core.object.entity.Message;
import everyos.bot.chat4j.ChatClient;
import everyos.bot.chat4j.ChatConnection;
import everyos.bot.chat4j.entity.ChatChannel;
import everyos.bot.chat4j.entity.ChatMember;
import everyos.bot.chat4j.entity.ChatMessage;
import everyos.bot.chat4j.entity.ChatUser;
import everyos.bot.chat4j.functionality.ChatInterface;
import everyos.bot.chat4j.functionality.message.ChatMessageReactionInterface;
import everyos.discord.chat4d.functionality.message.DiscordMessageReactionInterface;
import reactor.core.publisher.Mono;

public class DiscordMessage implements ChatMessage {
	private Message message;
	private ChatConnection connection;

	public DiscordMessage(ChatConnection connection, Message message) {
		this.message = message;
		this.connection = connection;
	}
	
	@Override
	public Optional<String> getContent() {
		return Optional.of(message.getContent());
	}

	@Override
	public Mono<Void> delete() {
		return message.delete();
	}

	@Override
	public Mono<ChatChannel> getChannel() {
		return message.getChannel()
			.map(member->new DiscordChannel(getConnection(), member));
	}

	@Override
	public Mono<ChatUser> getAuthor() {
		Optional<ChatUser> author = message.getAuthor()
			.map(member->new DiscordUser(getConnection(), member));
		if (author.isEmpty()) {
			return Mono.empty();
		}
		return Mono.just(author.get());
	}

	@Override
	public Mono<ChatMember> getAuthorAsMember() {
		return message.getAuthorAsMember()
			.map(member->DiscordMember.instatiate(getConnection(), member));
	}

	@Override
	public long getTimestamp() {
		return message.getTimestamp().toEpochMilli();
	}

	@Override
	public <T extends ChatInterface> boolean supportsInterface(Class<T> cls) {
		if (cls == ChatMessageReactionInterface.class) {
			return true;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends ChatInterface> T getInterface(Class<T> cls) {
		if (cls == ChatMessageReactionInterface.class) {
			return (T) new DiscordMessageReactionInterface(connection, message);
		}
		return null;
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
	public Mono<Void> pin() {
		return message.pin();
	}

	@Override
	public long getChannelID() {
		return message.getChannelId().asLong();
	}

	@Override
	public long getAuthorID() {
		if (!message.getAuthor().isPresent()) return -1L;
		return message.getAuthor().get().getId().asLong();
	}

	@Override
	public long getID() {
		return message.getId().asLong();
	}
}
