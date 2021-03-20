package everyos.bot.luwu.run.command.modules.starboard;

import everyos.bot.luwu.core.entity.Channel;
import everyos.bot.luwu.core.entity.ChannelID;
import everyos.bot.luwu.core.entity.EmojiID;
import everyos.bot.luwu.util.Tuple;
import reactor.core.publisher.Mono;

public interface StarboardInfo {
	EmojiID getStarEmoji();
	ChannelID getStarboardChannelID();
	Mono<Channel> getStarboardChannel();
	ChannelID[] getExcludedChannels();
	Tuple<ChannelID, Integer>[] getChannelOverrides();
	Tuple<Integer, EmojiID>[] getEmojiLevels();
	boolean enabled();
}
