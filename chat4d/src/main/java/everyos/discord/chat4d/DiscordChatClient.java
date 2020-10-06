package everyos.discord.chat4d;

import java.util.function.Function;

import discord4j.common.util.Snowflake;
import discord4j.core.DiscordClient;
import discord4j.core.event.EventDispatcher;
import discord4j.core.event.domain.Event;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.event.domain.message.MessageEvent;
import everyos.bot.chat4j.ChatClient;
import everyos.bot.chat4j.ChatConnection;
import everyos.bot.chat4j.entity.ChatUser;
import everyos.bot.chat4j.event.ChatEvent;
import everyos.bot.chat4j.event.ChatMessageCreateEvent;
import everyos.bot.chat4j.event.ChatMessageEvent;
import everyos.bot.chat4j.event.UnsupportedEventException;
import everyos.bot.chat4j.functionality.ChatInterface;
import everyos.discord.chat4d.entity.DiscordUser;
import everyos.discord.chat4d.event.DiscordEvent;
import everyos.discord.chat4d.event.DiscordMessageCreateEvent;
import everyos.discord.chat4d.event.DiscordMessageEvent;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

public class DiscordChatClient implements ChatClient {
	private DiscordClient client;

	public DiscordChatClient(String token) {
		this.client = DiscordClient.create(token);
	}
	
	@Override public Mono<Void> login(Function<ChatConnection, Mono<?>> func) {
		final ChatClient self = this;
		return client.withGateway(connection->{
			final EventDispatcher dispatcher = connection.getEventDispatcher();
			return func.apply(new ChatConnection() {
				@Override public Mono<Void> logout() {
					return connection.logout();
				}

				@Override public ChatClient getClient() {
					return self;
				}

				@Override public <T extends ChatEvent> boolean supportsEvent(Class<T> cls) {
					if (cls == ChatEvent.class) return true;
					if (cls == ChatMessageEvent.class) return true;
					if (cls == ChatMessageCreateEvent.class) return true;
					return false;
				}

				@Override public <T extends ChatEvent> Flux<T> generateEventListener(Class<T> cls) {
					Flux<?> m = null;
					if (cls == ChatEvent.class) {
						m = dispatcher.on(Event.class)
							.map(event->new DiscordEvent(this));
					}
					if (cls == ChatMessageEvent.class) {
						m = dispatcher.on(MessageEvent.class)
							.map(event->new DiscordMessageEvent(this));
					}
					if (cls == ChatMessageCreateEvent.class) {
						m = dispatcher.on(MessageCreateEvent.class)
							.map(event->new DiscordMessageCreateEvent(this, event));
					};
					
					if (m!=null) {
						return m
							.publishOn(Schedulers.boundedElastic())
							.cast(cls);
					}
					throw new UnsupportedEventException();
				}
				
				@Override public Mono<ChatUser> getUserByID(long id) {
					return connection.getUserById(Snowflake.of(id)).map(user->new DiscordUser(this, user));
				}
			});
		});
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
