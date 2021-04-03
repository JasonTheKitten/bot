package everyos.discord.chat4d.event;

import java.util.Optional;

import discord4j.core.event.domain.message.ReactionAddEvent;
import everyos.bot.chat4j.ChatConnection;
import everyos.bot.chat4j.entity.ChatGuild;
import everyos.bot.chat4j.entity.ChatMember;
import everyos.bot.chat4j.entity.ChatMessage;
import everyos.bot.chat4j.entity.ChatUser;
import everyos.bot.chat4j.event.ChatReactionAddEvent;
import everyos.discord.chat4d.entity.DiscordGuild;
import everyos.discord.chat4d.entity.DiscordMember;
import everyos.discord.chat4d.entity.DiscordMessage;
import everyos.discord.chat4d.entity.DiscordUser;
import reactor.core.publisher.Mono;

public class DiscordReactionAddEvent extends DiscordReactionEvent implements ChatReactionAddEvent {
	
	private ReactionAddEvent reactionEvent;

	public DiscordReactionAddEvent(ChatConnection connection, ReactionAddEvent reactionEvent) {
		super(connection, reactionEvent);
		this.reactionEvent = reactionEvent;
	}

	@Override
	public Optional<String> getReactionString() {
		return reactionEvent.getEmoji().asUnicodeEmoji().map(e->e.getRaw());
	}

	@Override
	public Optional<Long> getReactionLong() {
		return reactionEvent.getEmoji().asCustomEmoji().map(e->e.getId().asLong());
	}

	@Override
	public Mono<ChatMessage> getMessage() {
		return reactionEvent.getMessage().map(message->new DiscordMessage(getConnection(), message));
	}

	@Override
	public Mono<ChatUser> getAuthor() {
		return reactionEvent.getUser().map(user->new DiscordUser(getConnection(), user));
	}

	@Override
	public Mono<ChatMember> getAuthorAsMember() {
		return Mono.justOrEmpty(reactionEvent.getMember())
			.map(member->DiscordMember.instatiate(getConnection(), member));
	}

	@Override
	public Mono<ChatGuild> getGuild() {
		return reactionEvent.getGuild()
			.map(guild->new DiscordGuild(getConnection(), guild));
	}
}
