package everyos.bot.luwu.run.command.modules.role.autorole;

import java.util.Optional;

import everyos.bot.luwu.core.entity.RoleID;

public interface AutoroleInfo {
	Optional<RoleID> getUserRole();
	Optional<RoleID> getBotRole();
	RoleID[] getDefaultRoles();
	boolean hasRole(RoleID id);
}
