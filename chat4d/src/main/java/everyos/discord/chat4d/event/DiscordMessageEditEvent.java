package everyos.discord.chat4d.event;

import java.util.Optional;

import discord4j.core.event.domain.message.MessageUpdateEvent;
import everyos.bot.chat4j.ChatConnection;
import everyos.bot.chat4j.entity.ChatGuild;
import everyos.bot.chat4j.entity.ChatMessage;
import everyos.bot.chat4j.event.ChatMessageEditEvent;
import everyos.discord.chat4d.entity.DiscordGuild;
import everyos.discord.chat4d.entity.DiscordMessage;
import reactor.core.publisher.Mono;

public class DiscordMessageEditEvent extends DiscordMessageEvent implements ChatMessageEditEvent {

	private MessageUpdateEvent messageUpdateEvent;

	public DiscordMessageEditEvent(ChatConnection connection, MessageUpdateEvent event) {
		super(connection, event);
		
		this.messageUpdateEvent = event;
	}

	@Override
	public Optional<ChatMessage> getOldMessage() {
		return messageUpdateEvent.getOld()
			.map(message->new DiscordMessage(getConnection(), message));
	}
	
	@Override
	public Mono<ChatMessage> getMessage() {
		return messageUpdateEvent.getMessage()
			.map(message->new DiscordMessage(getConnection(), message));
	}

	@Override
	public Mono<ChatGuild> getGuild() {
		return messageUpdateEvent.getGuild()
			.map(guild->new DiscordGuild(getConnection(), guild));
	}
}
