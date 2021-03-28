package everyos.bot.luwu.run.command.modules.welcome;

import java.util.Optional;

import everyos.bot.luwu.core.entity.ChannelID;
import everyos.bot.luwu.util.Tuple;

public interface WelcomeServerInfo {
	Optional<Tuple<ChannelID, String>> getWelcomeMessage();
	Optional<Tuple<ChannelID, String>> getLeaveMessage();
}
