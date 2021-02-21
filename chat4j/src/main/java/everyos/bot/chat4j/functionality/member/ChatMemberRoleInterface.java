package everyos.bot.chat4j.functionality.member;

import everyos.bot.chat4j.functionality.ChatInterface;
import reactor.core.publisher.Mono;

public interface ChatMemberRoleInterface extends ChatInterface {
	/**
	 * Create a mono that adds a role to the member
	 * @param role The role to be added
	 * @param reason The reason for adding the role
	 * @return A mono that, when subscribed to, adds the role
	 */
	public Mono<Void> addRole(long role, String reason);
	
	/**
	 * Create a mono that removes a role to the member
	 * @param role The role to be added
	 * @param reason The reason for removing the role
	 * @return A mono that, when subscribed to, removes the role
	 */
	public Mono<Void> removeRole(long role, String reason);
}
