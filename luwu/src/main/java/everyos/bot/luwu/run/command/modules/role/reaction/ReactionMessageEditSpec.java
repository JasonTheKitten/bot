package everyos.bot.luwu.run.command.modules.role.reaction;

import everyos.bot.luwu.core.entity.EmojiID;
import everyos.bot.luwu.core.entity.RoleID;

public interface ReactionMessageEditSpec {
	public void addReaction(EmojiID emoji, RoleID role);
}
