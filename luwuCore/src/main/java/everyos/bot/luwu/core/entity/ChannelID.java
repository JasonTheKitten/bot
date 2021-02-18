package everyos.bot.luwu.core.entity;

import java.util.Objects;

import reactor.core.publisher.Mono;

public class ChannelID {
	private Connection connection;
	private long id;
	private int connectionID;

	public ChannelID(Connection connection, long id, int connectionID) {
		this.connection = connection;
		this.id = id;
		this.connectionID = connectionID;
	}
	
	public Mono<Channel> getChannel() {
		return connection.getBotEngine().getConnectionByID(connectionID).getChannelByID(this);
	};
	
	public long getLong() {
		return id;
	}

	public Connection getConnection() {
		return this.connection;
	}
	
	@Override
	public String toString() {
		return String.valueOf(id);
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof ChannelID)) {
			return false;
		}
		return
			((ChannelID) o).getLong() == this.getLong() &&
			((ChannelID) o).getConnectionID() == this.getConnectionID();
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(id, connectionID);
	}

	public int getConnectionID() {
		return connectionID;
	}
}
