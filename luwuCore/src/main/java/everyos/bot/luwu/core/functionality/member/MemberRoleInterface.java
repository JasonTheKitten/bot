package everyos.bot.luwu.core.functionality.member;

import everyos.bot.luwu.core.entity.RoleID;
import everyos.bot.luwu.core.functionality.Interface;
import reactor.core.publisher.Mono;

public interface MemberRoleInterface extends Interface {
	Mono<Void> addRole(RoleID role, String reason);
	Mono<Void> removeRole(RoleID role, String reason);
}
