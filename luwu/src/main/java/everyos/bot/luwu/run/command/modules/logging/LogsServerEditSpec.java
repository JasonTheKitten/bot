package everyos.bot.luwu.run.command.modules.logging;

import everyos.bot.luwu.core.entity.ChannelID;

public interface LogsServerEditSpec {
	void setLogChannel(ChannelID logChannelID);
	void clear();
}
