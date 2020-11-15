package everyos.bot.luwu.core.entity;

import java.util.Objects;

public class ChannelID {
	private long id;
	public ChannelID(long id) {
		this.id = id;
	}
	public long getLong() {
		return this.id;
	};
	
	@Override public boolean equals(Object object) {
		if (!(object instanceof ChannelID)) return false;
		ChannelID otherID = (ChannelID) object;
		return otherID.getLong()==getLong();
	}
	@Override public int hashCode() {
		return Objects.hash(id);
	}
	@Override public String toString() {
		return String.valueOf(id);
	}
}
