package everyos.discord.chat4d.event;

import java.util.Optional;

import discord4j.core.event.domain.message.MessageDeleteEvent;
import discord4j.core.object.entity.Message;
import everyos.bot.chat4j.ChatConnection;
import everyos.bot.chat4j.entity.ChatGuild;
import everyos.bot.chat4j.entity.ChatMessage;
import everyos.discord.chat4d.entity.DiscordGuild;
import everyos.discord.chat4d.entity.DiscordMessage;
import everyos.bot.chat4j.event.ChatMessageDeleteEvent;
import reactor.core.publisher.Mono;

public class DiscordMessageDeleteEvent extends DiscordMessageEvent implements ChatMessageDeleteEvent {

	private MessageDeleteEvent messageDeleteEvent;

	public DiscordMessageDeleteEvent(ChatConnection connection, MessageDeleteEvent event) {
		super(connection, event);
		
		this.messageDeleteEvent = event;
	}

	@Override
	public Mono<ChatMessage> getMessage() {
		Optional<Message> messageOp = messageDeleteEvent.getMessage();
		if (messageOp.isEmpty()) return Mono.empty();
		return Mono.just(new DiscordMessage(getConnection(), messageOp.get()));
	}

	@Override
	public Mono<ChatGuild> getGuild() {
		return messageDeleteEvent.getGuild()
			.map(guild->new DiscordGuild(getConnection(), guild));
	}

	@Override
	public Optional<String> getOldMessageContent() {
		return messageDeleteEvent.getMessage()
			.map(message->message.getContent());
	}

}
