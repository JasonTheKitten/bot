package everyos.bot.luwu.run.command.modules.starboard;

import everyos.bot.luwu.core.entity.ChannelID;
import everyos.bot.luwu.core.entity.EmojiID;
import everyos.bot.luwu.util.Tuple;

public interface StarboardInfo {
	EmojiID getStarEmoji();
	ChannelID getStarboardChannel();
	ChannelID[] getExcludedChannels();
	Tuple<ChannelID, Integer>[] getChannelOverrides();
	Tuple<Integer, EmojiID>[] getEmojiLevels();
	boolean enabled();
}
