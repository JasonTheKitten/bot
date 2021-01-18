package everyos.bot.luwu.core.entity;

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
}
