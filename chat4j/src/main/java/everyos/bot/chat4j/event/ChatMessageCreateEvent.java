package everyos.bot.chat4j.event;

import java.util.Optional;

import everyos.bot.chat4j.entity.ChatMember;
import everyos.bot.chat4j.entity.ChatMessage;
import everyos.bot.chat4j.entity.ChatUser;
import reactor.core.publisher.Mono;

public interface ChatMessageCreateEvent extends ChatMessageEvent {
	/**
	 * Return a mono that gets the message associated with this event
	 * @return A mono that, when subscribed to, returns the message associated with this event
	 */
	public Mono<ChatMessage> getMessage();
	
	/**
	 * Return a mono that gets the author of the message associated with this event
	 * @return A mono that, when subscribed to, returns the author of the message associated with this event
	 */
	public Mono<ChatUser> getSender();
	
	/**
	 * Return a mono that gets the author of the message associated with this event, as a member
	 * @return A mono that, when subscribed to, optionally returns the author of the message associated with this event, as a member
	 */
	public Mono<Optional<ChatMember>> getSenderAsMember();
	
	/**
	 * Return a mono that gets the guild that this event was triggered from
	 * @return A mono that, when subscribed to, returns the guild that this event was triggered from
	 */
	//public Mono<ChatGuild> getGuild();
}
