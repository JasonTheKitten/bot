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

	public Mono<User> getUserByID(long id) {
		return connection.getUserByID(id).map(user->new User(this, user));
	}
	public Mono<Channel> getChannelByID(long id) {
		return connection.getChannelByID(id).map(channel->new Channel(this, channel));
	}
	
	public Client getClient() {
		return client;
	}
	public BotEngine getBotEngine() {
		return client.getBotEngine();
	}

	public <T extends Event> Flux<MessageCreateEvent> generateEventListener(Class<T> cls) {
		//TODO: I don't like this
		if (cls==MessageCreateEvent.class) {
			return connection.generateEventListener(ChatMessageCreateEvent.class)
				.map(event->new MessageCreateEvent(this, event));
		}
		return null;
	}
	//TODO: SupportsEvent
}
