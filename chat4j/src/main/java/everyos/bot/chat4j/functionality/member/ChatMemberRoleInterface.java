package everyos.bot.chat4j.functionality.member;

import everyos.bot.chat4j.entity.ChatRole;
import everyos.bot.chat4j.functionality.ChatInterface;
import reactor.core.publisher.Mono;

public interface ChatMemberRoleInterface extends ChatInterface {
	/**
	 * Create a mono that adds a role to the member
	 * @param role The role to be added
	 * @return A mono that, when subscribed to, adds the role
	 */
	public Mono<Void> addRole(ChatRole role);
	
	/**
	 * Create a mono that removes a role to the member
	 * @param role The role to be added
	 * @return A mono that, when subscribed to, removes the role
	 */
	public Mono<Void> removeRole(ChatRole role);
}
