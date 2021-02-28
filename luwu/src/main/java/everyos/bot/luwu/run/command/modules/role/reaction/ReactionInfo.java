package everyos.bot.luwu.run.command.modules.role.reaction;

import java.util.Optional;

import everyos.bot.luwu.core.entity.EmojiID;
import everyos.bot.luwu.core.entity.RoleID;

public interface ReactionInfo {
	public Optional<RoleID> getReaction(EmojiID reaction);
}
