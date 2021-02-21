package everyos.bot.luwu.core.entity.imp;

import everyos.bot.chat4j.functionality.member.ChatMemberRoleInterface;
import everyos.bot.luwu.core.entity.Client;
import everyos.bot.luwu.core.entity.Connection;
import everyos.bot.luwu.core.entity.RoleID;
import everyos.bot.luwu.core.functionality.member.MemberRoleInterface;
import reactor.core.publisher.Mono;

public class MemberRoleInterfaceImp implements MemberRoleInterface {
	private Connection connection;
	private ChatMemberRoleInterface roleInterface;

	public MemberRoleInterfaceImp(Connection connection, ChatMemberRoleInterface roleInterface) {
		this.connection = connection;
		this.roleInterface = roleInterface;
	}

	@Override
	public Connection getConnection() {
		return connection;
	}

	@Override
	public Client getClient() {
		return connection.getClient();
	}

	@Override
	public Mono<Void> addRole(RoleID role, String reason) {
		return roleInterface.addRole(role.getLong(), reason);
	}
	
	@Override
	public Mono<Void> removeRole(RoleID role, String reason) {
		return roleInterface.removeRole(role.getLong(), reason);
	}
}
