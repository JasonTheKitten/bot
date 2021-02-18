package everyos.bot.luwu.core.entity;

import java.util.Objects;

import reactor.core.publisher.Mono;

public class ServerID {
	private Connection connection;
	private long sid;
	
	public ServerID(Connection connection, long sid) {
		this.connection = connection;
		this.sid = sid;
	}

	public long getLong() {
		return this.sid;
	};
	
	public Mono<Server> getServer() {
		return connection.getServerByID(this);
	};
	
	@Override
	public boolean equals(Object o) {
		//TODO: Also connection IDs
		return
			(o instanceof ServerID) &&
			(((ServerID) o).getLong() == getLong());
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(sid);
	}
}
