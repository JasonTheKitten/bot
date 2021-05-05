package everyos.discord.chat4d.event;

import discord4j.core.event.domain.guild.MemberLeaveEvent;
import everyos.bot.chat4j.ChatConnection;
import everyos.bot.chat4j.entity.ChatGuild;
import everyos.bot.chat4j.entity.ChatMember;
import everyos.bot.chat4j.event.ChatMemberLeaveEvent;
import everyos.discord.chat4d.entity.DiscordGuild;
import everyos.discord.chat4d.entity.DiscordMember;
import reactor.core.publisher.Mono;

public class DiscordMemberLeaveEvent extends DiscordMemberEvent implements ChatMemberLeaveEvent {
	private MemberLeaveEvent leaveEvent;

	public DiscordMemberLeaveEvent(ChatConnection connection, MemberLeaveEvent event) {
		super(connection, event);
		this.leaveEvent = event;
	}
	
	@Override
	public ChatMember getMember() {
		return DiscordMember.instatiate(getConnection(), leaveEvent.getMember().get());
		//TODO: Return an optional instead
	}

	@Override
	public Mono<ChatGuild> getGuild() {
		return leaveEvent.getGuild().map(guild->new DiscordGuild(getConnection(), guild));
	}
}
