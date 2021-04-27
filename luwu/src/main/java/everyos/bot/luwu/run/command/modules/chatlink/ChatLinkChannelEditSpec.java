package everyos.bot.luwu.run.command.modules.chatlink;

import everyos.bot.luwu.core.entity.UserID;

public interface ChatLinkChannelEditSpec {
	void setVerified(boolean b);
	void addMutedUser(UserID user);
	void removeMutedUser(UserID user);
}
