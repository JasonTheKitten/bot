package everyos.bot.luwu.core.entity;

import everyos.bot.chat4j.ChatConnection;
import everyos.bot.chat4j.event.ChatMessageCreateEvent;
import everyos.bot.luwu.core.BotEngine;
import everyos.bot.luwu.core.entity.event.Event;
import everyos.bot.luwu.core.entity.event.MessageCreateEvent;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class Connection {
	private ChatConnection connection;
	private Client client;
	public Connection(Client client, ChatConnection connection) {
		this.connection = connection;
		this.client = client;
	}

	protected Mono<User> getUserByID(UserID id) {
		return connection.getUserByID(id.getLong()).map(user->new User(this, user));
	}
	protected Mono<Channel> getChannelByID(ChannelID id) {
		return client.getBotEngine().getConnectionByID(id.getConnectionID()).connection
			.getChannelByID(id.getLong()).map(channel->new Channel(this, channel));
	}
	public Mono<Server> getServerByID(ServerID id) {
		return connection.getGuildByID(id.getLong()).map(server->new Server(this, server));
	}
	
	public Client getClient() {
		return client;
	}
	public BotEngine getBotEngine() {
		return client.getBotEngine();
	}

	@SuppressWarnings("unchecked")
	public <T extends Event> Flux<T> generateEventListener(Class<T> cls) {
		//TODO: I don't like this
		if (cls==MessageCreateEvent.class) {
			return (Flux<T>) connection.generateEventListener(ChatMessageCreateEvent.class)
				.map(event->new MessageCreateEvent(this, event));
		}
		return null;
	}
	//TODO: SupportsEvent
}
