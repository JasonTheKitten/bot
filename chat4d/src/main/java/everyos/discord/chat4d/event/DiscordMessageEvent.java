package everyos.discord.chat4d.event;

import discord4j.core.event.domain.message.MessageEvent;
import everyos.bot.chat4j.ChatConnection;
import everyos.bot.chat4j.event.ChatMessageEvent;

public abstract class DiscordMessageEvent extends DiscordEvent implements ChatMessageEvent {
	public DiscordMessageEvent(ChatConnection connection, MessageEvent event) {
		super(connection, event);
	}
}
