package everyos.bot.luwu.run.command.modules.role.role;

import java.util.Map;
import java.util.Optional;

import everyos.bot.luwu.core.entity.RoleID;

public interface RoleInfo {
	Optional<RoleID> getRole(String name);
	Map<String, RoleID> getAvailableRoles();
}
