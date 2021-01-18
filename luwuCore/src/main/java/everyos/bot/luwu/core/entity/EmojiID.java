package everyos.bot.luwu.core.entity;

import java.util.Optional;

public interface EmojiID {
	Optional<String> getName();
	Optional<Long> getID();
	
	static EmojiID of(String string) {
		return null;
	}
	static EmojiID of(long rid) {
		return null;
	}
}
