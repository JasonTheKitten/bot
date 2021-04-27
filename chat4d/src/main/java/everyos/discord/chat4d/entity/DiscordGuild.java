package everyos.discord.chat4d.entity;

import java.util.function.Consumer;

import discord4j.core.object.entity.Guild;
import everyos.bot.chat4j.ChatClient;
import everyos.bot.chat4j.ChatConnection;
import everyos.bot.chat4j.entity.ChatChannel;
import everyos.bot.chat4j.entity.ChatGuild;
import everyos.bot.chat4j.functionality.ChatInterface;
import everyos.bot.chat4j.functionality.channel.ChannelCreateSpec;
import reactor.core.publisher.Mono;

public class DiscordGuild implements ChatGuild {
	private Guild guild;
	private ChatConnection connection;

	public DiscordGuild(ChatConnection connection, Guild guild) {
		this.connection = connection;
		this.guild = guild;
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
	public ChatClient getClient() {
		return connection.getClient();
	}

	@Override
	public long getID() {
		return guild.getId().asLong();
	}

	@Override
	public String getName() {
		return guild.getName();
	}

	//TODO: Should this be under an interface?
	@Override
	public Mono<ChatChannel> createChannel(Consumer<ChannelCreateSpec> func) {
		return guild.createTextChannel(spec->{
			func.accept(new ChannelCreateSpec() {
				@Override
				public void setName(String name) {
					spec.setName(name);
				}
				
				@Override
				public void setTopic(String name) {
					spec.setTopic(name);
				}
			});
		}).map(channel->new DiscordChannel(connection, channel));
	}

}
