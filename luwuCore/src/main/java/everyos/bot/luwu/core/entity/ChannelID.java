package everyos.bot.luwu.core.entity;

import reactor.core.publisher.Mono;

public class ChannelID {
	private Connection connection;
	private long id;

	public ChannelID(Connection connection, long id) {
		this.connection = connection;
		this.id = id;
	}
	
	public Mono<Channel> getChannel() {
		return connection.getChannelByID(this);
	};
	
	public long getLong() {
		return id;
	}

	public Connection getConnection() {
		return this.connection;
	}
}
