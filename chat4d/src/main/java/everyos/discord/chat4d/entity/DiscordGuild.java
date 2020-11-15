package everyos.discord.chat4d.entity;

import discord4j.core.object.entity.Guild;
import everyos.bot.chat4j.ChatClient;
import everyos.bot.chat4j.ChatConnection;
import everyos.bot.chat4j.entity.ChatGuild;
import everyos.bot.chat4j.functionality.ChatInterface;

public class DiscordGuild implements ChatGuild {
	private Guild guild;
	private ChatConnection connection;

	public DiscordGuild(ChatConnection connection, Guild guild) {
		this.connection = connection;
		this.guild = guild;
	}

	@Override public <T extends ChatInterface> boolean supportsInterface(Class<T> cls) {
		return false;
	}

	@Override public <T extends ChatInterface> T getInterface(Class<T> cls) {
		return null;
	}

	@Override public ChatClient getClient() {
		return connection.getClient();
	}

	@Override public long getID() {
		return guild.getId().asLong();
	}

	@Override public String getName() {
		return guild.getName();
	}

}
