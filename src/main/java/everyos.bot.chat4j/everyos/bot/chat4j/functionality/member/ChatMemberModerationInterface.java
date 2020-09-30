package everyos.bot.chat4j.functionality.member;

import everyos.bot.chat4j.functionality.ChatInterface;
import reactor.core.publisher.Mono;

/**
 * This interface allows for running moderation actions on server/channel members
 */
public interface ChatMemberModerationInterface extends ChatInterface {
	/**
	 * Create a mono that kicks the member
	 * @param reason The reason for the member to be kicked
	 * @return A mono that, when subscribed to, kicks the user
	 */
	public Mono<Void> kick(String reason);
	
	/**
	 * Create a mono that bans the member
	 * @param reason The reason for the member to be banned
	 * @return A mono that, when subscribed to, bans the user
	 */
	public Mono<Void> ban(String reason);
	
	/**
	 * Create a mono that bans the member
	 * @param reason The reason for the member to be banned
	 * @param days The number of days worth of messages to be deleted
	 * @return A mono that, when subscribed to, bans the user
	 */
	public Mono<Void> ban(String reason, int days);
}
