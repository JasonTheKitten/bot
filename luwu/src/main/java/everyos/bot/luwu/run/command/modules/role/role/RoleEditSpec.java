package everyos.bot.luwu.run.command.modules.role.role;

import everyos.bot.luwu.core.entity.RoleID;

public interface RoleEditSpec {
	void setRole(String name, RoleID id);
	void reset();
	void removeRole(String roleName);
}
