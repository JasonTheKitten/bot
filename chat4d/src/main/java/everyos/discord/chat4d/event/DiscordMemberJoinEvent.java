package everyos.discord.chat4d.event;

import discord4j.core.event.domain.guild.MemberJoinEvent;
import everyos.bot.chat4j.ChatConnection;
import everyos.bot.chat4j.entity.ChatGuild;
import everyos.bot.chat4j.entity.ChatMember;
import everyos.bot.chat4j.event.ChatMemberJoinEvent;
import everyos.discord.chat4d.entity.DiscordGuild;
import everyos.discord.chat4d.entity.DiscordMember;
import reactor.core.publisher.Mono;

public class DiscordMemberJoinEvent extends DiscordEvent implements ChatMemberJoinEvent {
	private MemberJoinEvent joinEvent;

	public DiscordMemberJoinEvent(ChatConnection connection, MemberJoinEvent event) {
		super(connection, event);
		
		this.joinEvent = event;
	}
	
	@Override
	public ChatMember getMember() {
		return DiscordMember.instatiate(getConnection(), joinEvent.getMember());
	}

	@Override
	public Mono<ChatGuild> getGuild() {
		return joinEvent.getGuild().map(guild->new DiscordGuild(getConnection(), guild));
	}
}
