package everyos.nertivia.chat4n;

import everyos.bot.chat4j.ChatClient;
import everyos.bot.chat4j.ChatConnection;
import everyos.bot.chat4j.entity.ChatChannel;
import everyos.bot.chat4j.entity.ChatGuild;
import everyos.bot.chat4j.entity.ChatUser;
import everyos.bot.chat4j.event.ChatEvent;
import everyos.bot.chat4j.event.ChatMessageCreateEvent;
import everyos.bot.chat4j.event.ChatMessageEvent;
import everyos.bot.chat4j.event.ChatReactionAddEvent;
import everyos.bot.chat4j.event.ChatReactionEvent;
import everyos.bot.chat4j.event.ChatReactionRemoveEvent;
import everyos.bot.chat4j.status.StatusType;
import everyos.nertivia.chat4n.entity.NertiviaChannel;
import everyos.nertivia.chat4n.entity.NertiviaServer;
import everyos.nertivia.chat4n.entity.NertiviaUser;
import everyos.nertivia.chat4n.event.NertiviaMessageCreateEvent;
import everyos.nertivia.nertivia4j.NertiviaClient;
import everyos.nertivia.nertivia4j.NertiviaConnection;
import everyos.nertivia.nertivia4j.event.MessageCreateEvent;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

public class NertiviaChatConnection implements ChatConnection {
	private NertiviaClient client;
	private NertiviaChatClient chatClient;
	private NertiviaConnection connection;

	public NertiviaChatConnection(NertiviaChatClient chatClient, NertiviaClient client, NertiviaConnection connection) {
		this.client = client;
		this.chatClient = chatClient;
		this.connection = connection;
	}

	@Override
	public Mono<Void> logout() {
		return connection.logout();
	}

	@Override
	public ChatClient getClient() {
		return chatClient;
	}

	@Override
	public <T extends ChatEvent> boolean supportsEvent(Class<T> cls) {
		return
			cls==ChatEvent.class||
			cls==ChatMessageEvent.class||
			cls==ChatMessageCreateEvent.class;
	}

	@Override
	public <T extends ChatEvent> Flux<T> generateEventListener(Class<T> cls) {
		Flux<T> m = generateInternalEventListener(cls);
			
		if (m!=null) {
			return m
				.publishOn(Schedulers.boundedElastic())
				.cast(cls);
		}
		return Flux.empty();
	}
	
	@SuppressWarnings("unchecked")
	private <T extends ChatEvent> Flux<T> generateInternalEventListener(Class<T> cls) {
		//TODO: Maybe just remove the argument and automatically handle things
		
		if (cls == ChatEvent.class) {
			return (Flux<T>)
				generateInternalEventListener(ChatMessageEvent.class);
		} else if (cls == ChatMessageEvent.class) {
			return (Flux<T>) Flux.merge(
				generateInternalEventListener(ChatMessageCreateEvent.class),
				generateInternalEventListener(ChatReactionEvent.class));
		} else if (cls == ChatMessageCreateEvent.class) {
			return (Flux<T>) connection.listen(MessageCreateEvent.class)
				.map(event->new NertiviaMessageCreateEvent(this, event));
		} else if (cls == ChatReactionEvent.class) {
			return (Flux<T>) Flux.merge(
				generateInternalEventListener(ChatReactionAddEvent.class),
				generateInternalEventListener(ChatReactionRemoveEvent.class));
		} /*else if (cls == ChatReactionAddEvent.class) {
			return (Flux<T>) connection.listen(ReactionAddEvent.class)
				.map(event->new NertiviaReactionAddEvent(this, event));
		} else if (cls == ChatReactionRemoveEvent.class) {
			return (Flux<T>) connection.listen(ReactionRemoveEvent.class)
				.map(event->new NertiviaReactionRemoveEvent(this, event));
		}*/
		
		return null;
	}

	@Override
	public Mono<ChatUser> getUserByID(long uid) {
		return client.getUserByID(uid)
			.map(user->new NertiviaUser(this, user));
	}

	@Override
	public Mono<ChatChannel> getChannelByID(long cid) {
		return client.getChannelByID(cid)
			.map(channel->new NertiviaChannel(this, channel));
	}
	
	@Override
	public Mono<ChatGuild> getGuildByID(long cid) {
		return client.getServerByID(cid)
			.map(server->new NertiviaServer(this, server));
	}

	@Override
	public Mono<ChatUser> getSelfAsUser() {
		return client.getSelfAsUser()
			.map(user->new NertiviaUser(this, user));
	}

	@Override
	public long getSelfID() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean supportsStatus(StatusType type) {
		return false;
	}

	@Override
	public Mono<Void> setStatus(StatusType type, String text) {
		return Mono.empty();
	}
}
