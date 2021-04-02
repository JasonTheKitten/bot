package everyos.discord.chat4d.event;

import discord4j.core.event.domain.guild.GuildEvent;
import everyos.bot.chat4j.ChatConnection;
import everyos.bot.chat4j.event.ChatMemberEvent;

public abstract class DiscordMemberEvent extends DiscordEvent implements ChatMemberEvent {
	public DiscordMemberEvent(ChatConnection connection, GuildEvent event) {
		super(connection, event);
	}
}
