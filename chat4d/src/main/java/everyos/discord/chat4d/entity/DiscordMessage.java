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
import reactor.core.publisher.Mono;

public class DiscordMessage implements ChatMessage {
	private Message message;
	private ChatConnection connection;

	public DiscordMessage(ChatConnection connection, Message message) {
		this.message = message;
		this.connection = connection;
	}
	
	@Override public Optional<String> getContent() {
		return Optional.of(message.getContent());
	}

	@Override public Mono<Void> delete() {
		return message.delete();
	}

	@Override public Mono<ChatChannel> getChannel() {
		return message.getChannel()
			.map(member->new DiscordChannel(getConnection(), member));
	}

	@Override public Mono<ChatUser> getAuthor() {
		return message.getAuthorAsMember()
			.map(member->new DiscordUser(getConnection(), member));
	}

	@Override public Mono<ChatMember> getAuthorAsMember() {
		return message.getAuthorAsMember()
			.map(member->DiscordMember.instatiate(getConnection(), member));
	}

	@Override public long getTimestamp() {
		return message.getTimestamp().toEpochMilli();
	}

	@Override public <T extends ChatInterface> boolean supportsInterface(Class<T> cls) {
		return false;
	}

	@Override public <T extends ChatInterface> T getInterface(Class<T> cls) {
		return null;
	}

	@Override public ChatClient getClient() {
		return getConnection().getClient();
	}

	@Override public ChatConnection getConnection() {
		return connection;
	}
}
