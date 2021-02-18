package everyos.bot.luwu.core.entity;

import java.util.Optional;

public interface EmojiID {
	Optional<String> getName();
	Optional<Long> getID();
	
	//TODO: Override equals and hashCode
	
	static EmojiID of(String name) {
		return new EmojiID() {
			@Override
			public Optional<String> getName() {
				return Optional.of(name);
			}

			@Override
			public Optional<Long> getID() {
				return Optional.empty();
			}
		};
	}
	static EmojiID of(long rid) {
		return new EmojiID() {
			@Override
			public Optional<String> getName() {
				return Optional.empty();
			}

			@Override
			public Optional<Long> getID() {
				return Optional.of(rid);
			}
		};
	}
}
