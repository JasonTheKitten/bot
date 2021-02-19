package everyos.discord.chat4d.event;

import discord4j.core.event.domain.message.MessageEvent;
import everyos.bot.chat4j.ChatConnection;
import everyos.bot.chat4j.event.ChatReactionEvent;

public abstract class DiscordReactionEvent extends DiscordMessageEvent implements ChatReactionEvent {
	public DiscordReactionEvent(ChatConnection connection, MessageEvent reactionEvent) {
		super(connection, reactionEvent);
	}
}
