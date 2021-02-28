package everyos.bot.luwu.core.entity;

import java.util.Objects;

public class RoleID {
	@SuppressWarnings("unused")
	private Connection connection;
	private long rid;

	public RoleID(Connection connection, long rid) {
		this.connection = connection;
		this.rid = rid;
	}
	
	public long getLong() {
		return rid;
	}
	
	@Override
	public boolean equals(Object o) {
		return
			(o instanceof RoleID) &&
			(((RoleID) o).getLong() == getLong());
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(rid);
	}
	
	@Override
	public String toString() {
		return String.valueOf(rid);
	}
}
