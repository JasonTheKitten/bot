package everyos.bot.luwu.run.command.modules.role.autorole;

import everyos.bot.luwu.core.entity.RoleID;

public interface AutoroleEditSpec {
	void setUserRole(RoleID role);
	void setBotRole(RoleID role);
	void removeRole(RoleID role);
	void addDefaultRole(RoleID role);
	AutoroleInfo getInfo();
	void reset();
}
