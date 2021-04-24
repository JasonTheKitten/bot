package everyos.bot.luwu.core.entity;

import everyos.bot.chat4j.ChatConnection;
import everyos.bot.chat4j.event.ChatMemberJoinEvent;
import everyos.bot.chat4j.event.ChatMemberLeaveEvent;
import everyos.bot.chat4j.event.ChatMessageCreateEvent;
import everyos.bot.chat4j.event.ChatMessageDeleteEvent;
import everyos.bot.chat4j.event.ChatMessageEditEvent;
import everyos.bot.chat4j.event.ChatReactionAddEvent;
import everyos.bot.chat4j.event.ChatReactionRemoveEvent;
import everyos.bot.chat4j.event.ChatServerCreateEvent;
import everyos.bot.chat4j.event.ChatServerDeleteEvent;
import everyos.bot.chat4j.status.Status;
import everyos.bot.luwu.core.BotEngine;
import everyos.bot.luwu.core.entity.event.Event;
import everyos.bot.luwu.core.entity.event.MemberEvent;
import everyos.bot.luwu.core.entity.event.MemberJoinEvent;
import everyos.bot.luwu.core.entity.event.MemberLeaveEvent;
import everyos.bot.luwu.core.entity.event.MessageCreateEvent;
import everyos.bot.luwu.core.entity.event.MessageDeleteEvent;
import everyos.bot.luwu.core.entity.event.MessageEditEvent;
import everyos.bot.luwu.core.entity.event.MessageEvent;
import everyos.bot.luwu.core.entity.event.ReactionAddEvent;
import everyos.bot.luwu.core.entity.event.ReactionEvent;
import everyos.bot.luwu.core.entity.event.ReactionRemoveEvent;
import everyos.bot.luwu.core.entity.event.ServerCreateEvent;
import everyos.bot.luwu.core.entity.event.ServerDeleteEvent;
import everyos.bot.luwu.core.entity.event.ServerEvent;
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
	protected Mono<Server> getServerByID(ServerID id) {
		return connection.getGuildByID(id.getLong()).map(server->new Server(this, server));
	}
	
	public Mono<User> getSelfAsUser() {
		return connection.getSelfAsUser()
			.map(member->new User(this, member));
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
		if (cls==Event.class) {
			return (Flux<T>) Flux.merge(
				generateEventListener(MessageEvent.class),
				generateEventListener(MemberEvent.class),
				generateEventListener(ServerEvent.class));
		}
		if (cls==MessageEvent.class) {
			return (Flux<T>) Flux.merge(
				generateEventListener(MessageCreateEvent.class),
				generateEventListener(ReactionEvent.class),
				generateEventListener(MessageDeleteEvent.class),
				generateEventListener(MessageEditEvent.class));
		}
		if (cls==MessageCreateEvent.class) {
			return (Flux<T>) connection.generateEventListener(ChatMessageCreateEvent.class)
				.map(event->new MessageCreateEvent(this, event));
		}
		if (cls==ReactionEvent.class) {
			return (Flux<T>) Flux.merge(
				generateEventListener(ReactionAddEvent.class),
				generateEventListener(ReactionRemoveEvent.class));
		}
		if (cls==ReactionAddEvent.class) {
			return (Flux<T>) connection.generateEventListener(ChatReactionAddEvent.class)
				.map(event->new ReactionAddEvent(this, event));
		}
		if (cls==ReactionRemoveEvent.class) {
			return (Flux<T>) connection.generateEventListener(ChatReactionRemoveEvent.class)
				.map(event->new ReactionRemoveEvent(this, event));
		}
		if (cls==MemberEvent.class) {
			return (Flux<T>) Flux.merge(
				generateEventListener(MemberJoinEvent.class),
				generateEventListener(MemberLeaveEvent.class));
		}
		if (cls==MemberJoinEvent.class) {
			return (Flux<T>) connection.generateEventListener(ChatMemberJoinEvent.class)
				.map(event->new MemberJoinEvent(this, event));
		}
		if (cls==MemberLeaveEvent.class) {
			return (Flux<T>) connection.generateEventListener(ChatMemberLeaveEvent.class)
				.map(event->new MemberLeaveEvent(this, event));
		}
		if (cls==MessageDeleteEvent.class) {
			return (Flux<T>) connection.generateEventListener(ChatMessageDeleteEvent.class)
				.map(event->new MessageDeleteEvent(this, event));
		}
		if (cls==MessageEditEvent.class) {
			return (Flux<T>) connection.generateEventListener(ChatMessageEditEvent.class)
				.map(event->new MessageEditEvent(this, event));
		}
		if (cls==ServerEvent.class) {
			return (Flux<T>) Flux.merge(
				generateEventListener(ServerCreateEvent.class),
				generateEventListener(ServerDeleteEvent.class));
		}
		if (cls==ServerCreateEvent.class) {
			return (Flux<T>) connection.generateEventListener(ChatServerCreateEvent.class)
				.map(event->new ServerCreateEvent(this, event));
		}
		if (cls==ServerDeleteEvent.class) {
			return (Flux<T>) connection.generateEventListener(ChatServerDeleteEvent.class)
				.map(event->new ServerDeleteEvent(this, event));
		}
		return Flux.empty();
		
		//TODO: Consider using HashMap instead of this giant if chain
	}
	//TODO: SupportsEvent

    public Mono<Void> setStatus(Status[] status) {
    	if (status.length == 0) {
    		//TODO: Clear status
    		return Mono.empty();
    	}
    	for (int i=0; i<status.length; i++) {
    		if (connection.supportsStatus(status[i].getType())) {
    			return connection.setStatus(status[i].getType(), status[i].getText());
    		}
    	}
        return connection.setStatus(status[0].getType(), status[0].getText());
    }

	public long getSelfID() {
		return connection.getSelfID();
	}
}
