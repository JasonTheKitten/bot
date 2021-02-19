package everyos.nertivia.chat4n;

import java.util.function.Function;

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
import everyos.bot.chat4j.functionality.ChatInterface;
import everyos.nertivia.chat4n.entity.NertiviaChannel;
import everyos.nertivia.chat4n.entity.NertiviaServer;
import everyos.nertivia.chat4n.entity.NertiviaUser;
import everyos.nertivia.chat4n.event.NertiviaMessageCreateEvent;
import everyos.nertivia.nertivia4j.NertiviaClient;
import everyos.nertivia.nertivia4j.NertiviaClientBuilder;
import everyos.nertivia.nertivia4j.event.MessageCreateEvent;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

public class NertiviaChatClient implements ChatClient {
	private NertiviaClient client;

	public NertiviaChatClient(String token) {
		this.client = NertiviaClientBuilder.create(token).build();
	}

	@Override public Mono<Void> login(Function<ChatConnection, Mono<?>> func) {
		ChatClient self = this;
		return client.connect().map(connection->new ChatConnection() {
			@Override public Mono<Void> logout() {
				return connection.logout();
			}

			@Override public ChatClient getClient() {
				return self;
			}

			@Override public <T extends ChatEvent> boolean supportsEvent(Class<T> cls) {
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
		}).flatMap(func).then();
	}

	@Override public <T extends ChatInterface> boolean supportsInterface(Class<T> cls) {
		return false;
	}

	@Override public <T extends ChatInterface> T getInterface(Class<T> cls) {
		return null;
	}
	
	@Override public ChatClient getClient() {
		return this;
	}
}
