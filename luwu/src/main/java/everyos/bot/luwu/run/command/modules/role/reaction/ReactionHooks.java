package everyos.bot.luwu.run.command.modules.role.reaction;

import java.util.Optional;

import everyos.bot.luwu.core.entity.RoleID;
import everyos.bot.luwu.core.entity.event.ReactionAddEvent;
import everyos.bot.luwu.core.entity.event.ReactionEvent;
import everyos.bot.luwu.core.entity.event.ReactionRemoveEvent;
import everyos.bot.luwu.core.functionality.member.MemberRoleInterface;
import reactor.core.publisher.Mono;

public class ReactionHooks {
	public static Mono<Void> reactionHook(ReactionEvent event) {
		//TODO: Do not respond to bots
		
		return event.getMessage()
			.flatMap(m->m.as(ReactionMessage.type))
			.flatMap(message->message.getInfo())
			.flatMap(reactionInfo->{
				Optional<RoleID> roleID = reactionInfo.getReaction(event.getReaction());
				if (roleID.isEmpty()) return Mono.empty();
				return event.getAuthorAsMember()
					.filter(member->!member.isBot())
					.flatMap(member->{
						MemberRoleInterface intf = member.getInterface(MemberRoleInterface.class);
						
						if (event instanceof ReactionAddEvent) {
							return intf.addRole(roleID.get(), "Reaction roles");
						} else if (event instanceof ReactionRemoveEvent) {
							return intf.removeRole(roleID.get(), "Reaction roles");
						}
						
						return Mono.empty();
					});
			})
			.then();
	}
}
