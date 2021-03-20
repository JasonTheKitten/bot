package everyos.bot.luwu.core.entity;

import java.util.Objects;
import java.util.Optional;

public interface EmojiID {
	Optional<String> getName();
	Optional<Long> getID();
	String getFormatted();
	
	//TODO: Override equals and hashCode
	
	static EmojiID of(String name) {
		try {
			return EmojiID.of(Long.valueOf(name));
		} catch (NumberFormatException e) {}
		
		return new EmojiID() {
			@Override
			public Optional<String> getName() {
				return Optional.of(name);
			}

			@Override
			public Optional<Long> getID() {
				return Optional.empty();
			}
			
			@Override
			public String toString() {
				return name;
			}

			@Override
			public String getFormatted() {
				return name;
			}
			
			@Override
			public boolean equals(Object o) {
				if (!(o instanceof EmojiID)) return false;
				EmojiID emoji = (EmojiID) o;
				if (!emoji.getName().isPresent()) return false;
				return name.equals(emoji.getName().get());
			}
			
			@Override
			public int hashCode() {
				return Objects.hash(name);
			}
		};
	}
	static EmojiID of(long eid) {
		return new EmojiID() {
			@Override
			public Optional<String> getName() {
				return Optional.empty();
			}

			@Override
			public Optional<Long> getID() {
				return Optional.of(eid);
			}
			@Override
			public String toString() {
				return String.valueOf(eid);
			}

			@Override
			public String getFormatted() {
				return "<:star:"+toString()+">";
			}
			
			@Override
			public boolean equals(Object o) {
				if (!(o instanceof EmojiID)) return false;
				EmojiID emoji = (EmojiID) o;
				if (!emoji.getID().isPresent()) return false;
				return eid==emoji.getID().get();
			}
			
			@Override
			public int hashCode() {
				return Objects.hash(eid);
			}
		};
	}
}
