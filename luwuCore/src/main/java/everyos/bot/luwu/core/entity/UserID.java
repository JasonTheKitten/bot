package everyos.bot.luwu.core.entity;

import java.util.Objects;

import reactor.core.publisher.Mono;

public class UserID {
	private long uid;
	private Connection connection;
	
	public UserID(Connection connection, long uid) {
		this.connection = connection;
		this.uid = uid;
	}
	
	public long getLong() {
		return this.uid;
	};
	public Mono<User> getUser() {
		return connection.getUserByID(this);
	};
	
	@Override
	public boolean equals(Object o) {
		//TODO: Also connection IDs
		return
			(o instanceof UserID) &&
			(((UserID) o).getLong() == getLong());
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(uid);
	}
	
	@Override
	public String toString() {
		return String.valueOf(uid);
	}
}
