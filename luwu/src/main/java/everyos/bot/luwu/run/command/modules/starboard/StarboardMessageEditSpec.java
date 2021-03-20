package everyos.bot.luwu.run.command.modules.starboard;

import everyos.bot.luwu.core.entity.Message;

public interface StarboardMessageEditSpec {
	void setStarboardMessage(Message message);
	void setOriginalMessage(Message message);
}
