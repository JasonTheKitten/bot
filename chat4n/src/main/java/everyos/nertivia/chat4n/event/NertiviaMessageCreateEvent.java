package everyos.nertivia.chat4n.event;

import java.util.Optional;

import everyos.bot.chat4j.ChatConnection;
import everyos.bot.chat4j.entity.ChatMember;
import everyos.bot.chat4j.entity.ChatMessage;
import everyos.bot.chat4j.entity.ChatUser;
import everyos.bot.chat4j.event.ChatMessageCreateEvent;
import everyos.nertivia.chat4n.entity.NertiviaMessage;
import everyos.nertivia.chat4n.entity.NertiviaUser;
import everyos.nertivia.nertivia4j.event.MessageCreateEvent;
import reactor.core.publisher.Mono;

public class NertiviaMessageCreateEvent extends NertiviaMessageEvent implements ChatMessageCreateEvent {
	private MessageCreateEvent event;

	public NertiviaMessageCreateEvent(ChatConnection connection, MessageCreateEvent event) {
		super(connection);
		this.event = event;
	}

	@Override public Mono<ChatMessage> getMessage() {
		return Mono.just(new NertiviaMessage(getConnection(), event.getMessage()));
	}

	@Override public Mono<ChatUser> getSender() {
		return event.getMessage().getAuthor().map(author->new NertiviaUser(getConnection(), author));
	}

	@Override public Mono<Optional<ChatMember>> getSenderAsMember() {
		return Mono.just(Optional.empty());
	}
}
