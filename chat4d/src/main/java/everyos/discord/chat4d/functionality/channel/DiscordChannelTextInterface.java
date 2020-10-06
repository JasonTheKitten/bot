package everyos.discord.chat4d.functionality.channel;

import java.util.function.Consumer;

import discord4j.core.object.entity.channel.TextChannel;
import everyos.bot.chat4j.ChatClient;
import everyos.bot.chat4j.ChatConnection;
import everyos.bot.chat4j.entity.ChatMessage;
import everyos.bot.chat4j.functionality.channel.ChatChannelTextInterface;
import everyos.bot.chat4j.functionality.message.MessageCreateSpec;
import everyos.discord.chat4d.entity.DiscordMessage;
import reactor.core.publisher.Mono;

public class DiscordChannelTextInterface implements ChatChannelTextInterface {
	private TextChannel channel;
	private ChatConnection connection;

	public DiscordChannelTextInterface(ChatConnection connection, TextChannel channel) {
		this.channel = channel;
		this.connection = connection;
	}

	@Override public Mono<ChatMessage> send(String text) {
		return channel.createMessage(text).map(message->new DiscordMessage(getConnection(), message));
	}
	@Override public Mono<ChatMessage> send(Consumer<MessageCreateSpec> spec) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override public ChatConnection getConnection() {
		return connection;
	}

	@Override public ChatClient getClient() {
		return getConnection().getClient();
	}
}
