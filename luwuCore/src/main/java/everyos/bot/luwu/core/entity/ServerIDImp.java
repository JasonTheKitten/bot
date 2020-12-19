package everyos.bot.luwu.core.entity;

import java.util.Objects;

public class ServerIDImp implements ServerID {
	private long longID;

	public ServerIDImp(long id) {
		this.longID = id;
	}

	@Override
	public long getLong() {
		return longID;
	}
	
	@Override public boolean equals(Object o) {
		return
			(o instanceof ServerID)&&
			((ServerID) o).getLong()==longID;
	}
	
	@Override public int hashCode() {
		return Objects.hash(longID);
	}
}
