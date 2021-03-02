package everyos.bot.luwu.run.command.modules.starboard;

import everyos.bot.luwu.core.entity.ChannelID;
import everyos.bot.luwu.core.entity.EmojiID;

public interface StarboardEditSpec {
	void setReaction(EmojiID id);
	void setOutputChannel(ChannelID id);
	StarboardInfo getInfo();
	void addChannelOverride(ChannelID channelID, int requiredStars);
	void addEmojiLevel(int level, EmojiID emoji);
}