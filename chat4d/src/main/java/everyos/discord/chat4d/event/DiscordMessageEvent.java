package everyos.discord.chat4d.event;

import everyos.bot.chat4j.ChatConnection;
import everyos.bot.chat4j.event.ChatMessageEvent;

public class DiscordMessageEvent extends DiscordEvent implements ChatMessageEvent{
	public DiscordMessageEvent(ChatConnection connection) {
		super(connection);
	}
}
