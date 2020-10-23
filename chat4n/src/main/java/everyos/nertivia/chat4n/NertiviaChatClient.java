package everyos.nertivia.chat4n;

import java.util.function.Function;

import everyos.bot.chat4j.ChatClient;
import everyos.bot.chat4j.ChatConnection;
import everyos.bot.chat4j.entity.ChatChannel;
import everyos.bot.chat4j.entity.ChatUser;
import everyos.bot.chat4j.event.ChatEvent;
import everyos.bot.chat4j.event.ChatMessageCreateEvent;
import everyos.bot.chat4j.event.ChatMessageEvent;
import everyos.bot.chat4j.event.UnsupportedEventException;
import everyos.bot.chat4j.functionality.ChatInterface;
import everyos.nertivia.chat4n.entity.NertiviaChannel;
import everyos.nertivia.chat4n.entity.NertiviaUser;
import everyos.nertivia.chat4n.event.NertiviaEvent;
import everyos.nertivia.chat4n.event.NertiviaMessageCreateEvent;
import everyos.nertivia.chat4n.event.NertiviaMessageEvent;
import everyos.nertivia.nertivia4j.NertiviaClient;
import everyos.nertivia.nertivia4j.NertiviaClientBuilder;
import everyos.nertivia.nertivia4j.event.Event;
import everyos.nertivia.nertivia4j.event.MessageCreateEvent;
import everyos.nertivia.nertivia4j.event.MessageEvent;
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
		return client.connect().map(c->new ChatConnection() {
			@Override public Mono<Void> logout() {
				return c.logout();
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

			@Override public <T extends ChatEvent> Flux<T> generateEventListener(Class<T> cls) {
				Flux<?> m = null;
				if (cls == ChatEvent.class) {
					m = c.listen(Event.class)
						.map(event->new NertiviaEvent(this));
				}
				if (cls == ChatMessageEvent.class) {
					m = c.listen(MessageEvent.class)
						.map(event->new NertiviaMessageEvent(this));
				}
				if (cls == ChatMessageCreateEvent.class) {
					m = c.listen(MessageCreateEvent.class)
						.map(event->new NertiviaMessageCreateEvent(this, event));
				};
				
				if (m!=null) {
					return m
						.publishOn(Schedulers.boundedElastic())
						.cast(cls);
				}
				throw new UnsupportedEventException();
			}

			@Override public Mono<ChatUser> getUserByID(long uid) {
				return client.getUserByID(uid)
					.map(user->new NertiviaUser(this, user));
			}

			@Override public Mono<ChatChannel> getChannelByID(long cid) {
				return client.getChannelByID(cid)
					.map(channel->new NertiviaChannel(this, channel));
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
