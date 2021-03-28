package everyos.bot.luwu.run.command.modules.welcome;

import everyos.bot.luwu.core.entity.ChannelID;

public interface WelcomeServerEditSpec {
	void setWelcomeMessage(ChannelID output, String message);
	void setLeaveMessage(ChannelID output, String message);
	WelcomeServerInfo getInfo();
}
