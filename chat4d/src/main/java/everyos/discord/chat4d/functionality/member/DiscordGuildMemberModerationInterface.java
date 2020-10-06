package everyos.discord.chat4d.functionality.member;

import discord4j.core.object.entity.Member;
import everyos.bot.chat4j.ChatClient;
import everyos.bot.chat4j.ChatConnection;
import everyos.bot.chat4j.functionality.member.ChatMemberModerationInterface;
import reactor.core.publisher.Mono;

public class DiscordGuildMemberModerationInterface implements ChatMemberModerationInterface {
	private Member member;
	private ChatConnection connection;

	public DiscordGuildMemberModerationInterface(ChatConnection connection, Member member) {
		this.member = member;
		this.connection = connection;
	}
	
	@Override public Mono<Void> kick(String reason) {
		return member.kick(reason);
	}

	@Override public Mono<Void> ban(String reason) {
		return ban(reason, 0);
	}

	@Override public Mono<Void> ban(String reason, int days) {
		return member.ban(spec->{
			spec.setReason(reason);
			spec.setDeleteMessageDays(days);
		});
	}

	@Override public ChatConnection getConnection() {
		return connection;
	}

	@Override public ChatClient getClient() {
		return getConnection().getClient();
	}
}
