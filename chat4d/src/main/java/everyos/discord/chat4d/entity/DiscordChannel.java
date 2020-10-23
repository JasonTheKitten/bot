package everyos.discord.chat4d.entity;

import discord4j.core.object.entity.channel.Channel;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.object.entity.channel.TextChannel;
import everyos.bot.chat4j.ChatClient;
import everyos.bot.chat4j.ChatConnection;
import everyos.bot.chat4j.entity.ChatChannel;
import everyos.bot.chat4j.entity.ChatGuild;
import everyos.bot.chat4j.functionality.ChatInterface;
import everyos.bot.chat4j.functionality.UnsupportedInterfaceException;
import everyos.bot.chat4j.functionality.channel.ChatChannelTextInterface;
import everyos.discord.chat4d.functionality.channel.DiscordChannelTextInterface;
import reactor.core.publisher.Mono;

public class DiscordChannel implements ChatChannel {
	private Channel channel;
	private ChatConnection connection;

	public DiscordChannel(ChatConnection connection, Channel channel) {
		this.channel = channel;
		this.connection = connection;
	}

	@Override public <T extends ChatInterface> boolean supportsInterface(Class<T> cls) {
		return
			(cls==ChatChannelTextInterface.class&&channel instanceof TextChannel);
	}

	@SuppressWarnings("unchecked")
	@Override public <T extends ChatInterface> T getInterface(Class<T> cls) {
		if (cls==ChatChannelTextInterface.class&&channel instanceof MessageChannel) {
			return (T) new DiscordChannelTextInterface(getConnection(), (MessageChannel) channel);
		}
		throw new UnsupportedInterfaceException();
	}

	@Override public ChatClient getClient() {
		return getConnection().getClient();
	}

	@Override public long getID() {
		return channel.getId().asLong();
	}

	@Override public ChatConnection getConnection() {
		return connection;
	}

	protected Channel getChannel() {
		return channel;
	}

	@Override public Mono<ChatGuild> getGuild() {
		// TODO Auto-generated method stub
		return Mono.empty();
	}
}
