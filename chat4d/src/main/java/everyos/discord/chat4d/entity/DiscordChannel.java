package everyos.discord.chat4d.entity;

import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.channel.Channel;
import discord4j.core.object.entity.channel.GuildChannel;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.object.entity.channel.PrivateChannel;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.object.entity.channel.VoiceChannel;
import everyos.bot.chat4j.ChatClient;
import everyos.bot.chat4j.ChatConnection;
import everyos.bot.chat4j.entity.ChatChannel;
import everyos.bot.chat4j.entity.ChatGuild;
import everyos.bot.chat4j.entity.ChatMessage;
import everyos.bot.chat4j.functionality.ChatInterface;
import everyos.bot.chat4j.functionality.UnsupportedInterfaceException;
import everyos.bot.chat4j.functionality.channel.ChatChannelTextInterface;
import everyos.bot.chat4j.functionality.channel.ChatChannelVoiceInterface;
import everyos.discord.chat4d.functionality.channel.DiscordChannelTextInterface;
import everyos.discord.chat4d.functionality.channel.DiscordChannelVoiceInterface;
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
		if (cls==ChatChannelVoiceInterface.class&&channel instanceof VoiceChannel) {
			VoiceChannel voiceChannel = (VoiceChannel) channel;
			return (T) new DiscordChannelVoiceInterface(getConnection(), voiceChannel);
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
		//TODO
		return ((GuildChannel) channel).getGuild().map(guild->new DiscordGuild(connection, guild));
	}

	@Override public String getName() {
		//TODO
		return ((GuildChannel) channel).getName();
	}

	@Override
	public boolean isPrivate() {
		return channel instanceof PrivateChannel;
	}

	@Override
	public Mono<ChatMessage> getMessageByID(long id) {
		return channel.getClient().getMessageById(channel.getId(), Snowflake.of(id))
			.map(message->new DiscordMessage(connection, message));
	}
}
