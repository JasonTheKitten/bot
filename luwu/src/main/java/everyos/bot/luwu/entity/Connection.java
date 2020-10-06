package everyos.bot.luwu.entity;

import everyos.bot.chat4j.ChatConnection;
import everyos.bot.luwu.BotInstance;
import reactor.core.publisher.Mono;

public class Connection {
	private BotInstance bot;
	private ChatConnection connection;
	public Connection(BotInstance bot, ChatConnection connection) {
		this.bot = bot;
		this.connection = connection;
	}

	public Mono<User> getUserByID(long id) {
		return Mono.empty();
	}
	
	public Client getClient() {
		return new Client(bot, connection.getClient());
	}
	public BotInstance getBot() {
		return this.bot;
	}
}
