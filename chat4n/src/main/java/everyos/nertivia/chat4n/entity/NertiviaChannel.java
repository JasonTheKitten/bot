package everyos.nertivia.chat4n.entity;

import everyos.bot.chat4j.ChatClient;
import everyos.bot.chat4j.ChatConnection;
import everyos.bot.chat4j.entity.ChatChannel;
import everyos.bot.chat4j.entity.ChatGuild;
import everyos.bot.chat4j.functionality.ChatInterface;
import everyos.bot.chat4j.functionality.UnsupportedInterfaceException;
import everyos.bot.chat4j.functionality.channel.ChatChannelTextInterface;
import everyos.nertivia.chat4n.functionality.channel.NertiviaTextInterface;
import everyos.nertivia.nertivia4j.entity.channel.Channel;
import everyos.nertivia.nertivia4j.entity.channel.MessageChannel;
import reactor.core.publisher.Mono;

public class NertiviaChannel implements ChatChannel {
	private Channel channel;
	private ChatConnection connection;

	public NertiviaChannel(ChatConnection connection, Channel channel) {
		this.channel = channel;
		this.connection = connection;
	}

	@Override public <T extends ChatInterface> boolean supportsInterface(Class<T> cls) {
		return
			(cls==ChatChannelTextInterface.class&&channel instanceof MessageChannel);
	}

	@SuppressWarnings("unchecked")
	@Override public <T extends ChatInterface> T getInterface(Class<T> cls) {
		if (cls==ChatChannelTextInterface.class&&channel instanceof MessageChannel) {
			return (T) new NertiviaTextInterface(getConnection(), (MessageChannel) channel);
		}
		throw new UnsupportedInterfaceException();
	}
	
	@Override public ChatClient getClient() {
		return connection.getClient();
	}

	@Override public long getID() {
		return channel.getID();
	}

	@Override public ChatConnection getConnection() {
		return connection;
	}

	@Override public Mono<ChatGuild> getGuild() {
		return Mono.empty();
	}
}
