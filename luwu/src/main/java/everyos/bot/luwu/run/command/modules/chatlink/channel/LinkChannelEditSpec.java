package everyos.bot.luwu.run.command.modules.chatlink.channel;

import everyos.bot.luwu.core.entity.UserID;

public interface LinkChannelEditSpec {
	
	void setVerified(boolean b);
	void setOpted(boolean b);
	void addMutedUser(UserID user);
	void removeMutedUser(UserID user);
	void setLinkID(long id);
	
}
