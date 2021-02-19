package everyos.discord.chat4d.event;

import java.util.Optional;

import discord4j.core.event.domain.message.MessageCreateEvent;
import everyos.bot.chat4j.ChatConnection;
import everyos.bot.chat4j.entity.ChatMember;
import everyos.bot.chat4j.entity.ChatMessage;
import everyos.bot.chat4j.entity.ChatUser;
import everyos.bot.chat4j.event.ChatMessageCreateEvent;
import everyos.discord.chat4d.entity.DiscordMember;
import everyos.discord.chat4d.entity.DiscordMessage;
import reactor.core.publisher.Mono;

public class DiscordMessageCreateEvent extends DiscordMessageEvent implements ChatMessageCreateEvent {
	private MessageCreateEvent event;

	public DiscordMessageCreateEvent(ChatConnection connection, MessageCreateEvent event) {
		super(connection, event);
		this.event = event;
	}

	@Override
	public Mono<ChatMessage> getMessage() {
		return Mono.just(new DiscordMessage(getConnection(), event.getMessage()));
	}

	@Override
	public Mono<ChatUser> getSender() {
		return getMessage().flatMap(msg->msg.getAuthor());
	}

	@Override
	public Mono<Optional<ChatMember>> getSenderAsMember() {
		if (event.getMember().isPresent()) {
			return Mono.just(DiscordMember.instatiate(getConnection(), event.getMember().get())).cast(ChatMember.class)
				.map(m->Optional.of(m));
		}
		return Mono.just(Optional.empty());
	}
}
