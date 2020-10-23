package everyos.nertivia.chat4n.functionality.channel;

import java.util.function.Consumer;

import everyos.bot.chat4j.ChatClient;
import everyos.bot.chat4j.ChatConnection;
import everyos.bot.chat4j.entity.ChatMessage;
import everyos.bot.chat4j.functionality.channel.ChatChannelTextInterface;
import everyos.bot.chat4j.functionality.message.MessageCreateSpec;
import everyos.nertivia.chat4n.entity.NertiviaMessage;
import everyos.nertivia.nertivia4j.entity.channel.MessageChannel;
import reactor.core.publisher.Mono;

public class NertiviaTextInterface implements ChatChannelTextInterface {
	private MessageChannel channel;
	private ChatConnection connection;

	public NertiviaTextInterface(ChatConnection connection, MessageChannel channel) {
		this.channel = channel;
		this.connection = connection;
	}

	@Override public Mono<ChatMessage> send(String text) {
		return channel.send(text).map(message->new NertiviaMessage(getConnection(), message));
	}
	@Override public Mono<ChatMessage> send(Consumer<MessageCreateSpec> spec) {
		// TODO Auto-generated method stub
		return Mono.empty();
	}

	@Override public ChatConnection getConnection() {
		return connection;
	}
	@Override public ChatClient getClient() {
		return getConnection().getClient();
	}
}
