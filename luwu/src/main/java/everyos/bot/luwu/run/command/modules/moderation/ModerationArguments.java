package everyos.bot.luwu.run.command.modules.moderation;

import everyos.bot.luwu.core.entity.UserID;

public interface ModerationArguments {
	public UserID[] getUsers();
	public String getReason();
}
