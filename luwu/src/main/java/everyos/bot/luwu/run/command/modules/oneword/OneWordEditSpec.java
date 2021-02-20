package everyos.bot.luwu.run.command.modules.oneword;

import everyos.bot.luwu.core.entity.UserID;

public interface OneWordEditSpec {
	void setMessage(String message);
	void setLastUser(UserID id);
}
