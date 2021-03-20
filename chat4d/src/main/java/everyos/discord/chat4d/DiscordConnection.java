package everyos.discord.chat4d;

import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.EventDispatcher;
import discord4j.core.event.domain.guild.MemberJoinEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.event.domain.message.ReactionAddEvent;
import discord4j.core.event.domain.message.ReactionRemoveEvent;
import everyos.bot.chat4j.ChatClient;
import everyos.bot.chat4j.ChatConnection;
import everyos.bot.chat4j.entity.ChatChannel;
import everyos.bot.chat4j.entity.ChatGuild;
import everyos.bot.chat4j.entity.ChatUser;
import everyos.bot.chat4j.event.ChatEvent;
import everyos.bot.chat4j.event.ChatMemberJoinEvent;
import everyos.bot.chat4j.event.ChatMessageCreateEvent;
import everyos.bot.chat4j.event.ChatMessageEvent;
import everyos.bot.chat4j.event.ChatReactionAddEvent;
import everyos.bot.chat4j.event.ChatReactionEvent;
import everyos.bot.chat4j.event.ChatReactionRemoveEvent;
import everyos.discord.chat4d.entity.DiscordChannel;
import everyos.discord.chat4d.entity.DiscordGuild;
import everyos.discord.chat4d.entity.DiscordUser;
import everyos.discord.chat4d.event.DiscordMemberJoinEvent;
import everyos.discord.chat4d.event.DiscordMessageCreateEvent;
import everyos.discord.chat4d.event.DiscordReactionAddEvent;
import everyos.discord.chat4d.event.DiscordReactionRemoveEvent;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

public class DiscordConnection implements ChatConnection {
	private final DiscordChatClient client;
	private final GatewayDiscordClient connection;
	private final EventDispatcher dispatcher;

	public DiscordConnection(DiscordChatClient client, GatewayDiscordClient connection) {
		this.client = client;
		this.connection = connection;
		this.dispatcher = connection.getEventDispatcher();
	}

	@Override
	public Mono<Void> logout() {
		return connection.logout();
	}

	@Override
	public ChatClient getClient() {
		return client;
	}

	@Override
	public <T extends ChatEvent> boolean supportsEvent(Class<T> cls) {
		if (cls == ChatEvent.class) return true;
		if (cls == ChatMessageEvent.class) return true;
		if (cls == ChatMessageCreateEvent.class) return true;
		return false;
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
			return (Flux<T>) Flux.merge(
				generateInternalEventListener(ChatMessageEvent.class),
				generateInternalEventListener(ChatMemberJoinEvent.class));
		} else if (cls == ChatMessageEvent.class) {
			return (Flux<T>) Flux.merge(
				generateInternalEventListener(ChatMessageCreateEvent.class),
				generateInternalEventListener(ChatReactionEvent.class));
		} else if (cls == ChatMessageCreateEvent.class) {
			return (Flux<T>) dispatcher.on(MessageCreateEvent.class)
				.map(event->new DiscordMessageCreateEvent(this, event));
		} else if (cls == ChatReactionEvent.class) {
			return (Flux<T>) Flux.merge(
				generateInternalEventListener(ChatReactionAddEvent.class),
				generateInternalEventListener(ChatReactionRemoveEvent.class));
		} else if (cls == ChatReactionAddEvent.class) {
			return (Flux<T>) dispatcher.on(ReactionAddEvent.class)
				.map(event->new DiscordReactionAddEvent(this, event));
		} else if (cls == ChatReactionRemoveEvent.class) {
			return (Flux<T>) dispatcher.on(ReactionRemoveEvent.class)
				.map(event->new DiscordReactionRemoveEvent(this, event));
		} else if (cls == ChatMemberJoinEvent.class) {
			return (Flux<T>) dispatcher.on(MemberJoinEvent.class)
				.map(event->new DiscordMemberJoinEvent(this, event));
		}
		
		return null;
	}
	
	@Override
	public Mono<ChatUser> getUserByID(long id) {
		return connection.getUserById(Snowflake.of(id)).map(user->new DiscordUser(this, user));
	}

	@Override
	public Mono<ChatChannel> getChannelByID(long id) {
		return connection.getChannelById(Snowflake.of(id)).map(user->new DiscordChannel(this, user));
	}
	
	@Override
	public Mono<ChatGuild> getGuildByID(long id) {
		return connection.getGuildById(Snowflake.of(id)).map(guild->new DiscordGuild(this, guild));
	}
}
