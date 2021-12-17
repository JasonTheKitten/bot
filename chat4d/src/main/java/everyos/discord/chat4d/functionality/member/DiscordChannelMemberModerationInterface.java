package everyos.discord.chat4d.functionality.member;

import discord4j.core.object.entity.Member;
import discord4j.core.spec.BanQuerySpec;
import everyos.bot.chat4j.ChatClient;
import everyos.bot.chat4j.ChatConnection;
import everyos.bot.chat4j.functionality.member.ChatMemberModerationInterface;
import reactor.core.publisher.Mono;

public class DiscordChannelMemberModerationInterface implements ChatMemberModerationInterface {
	
	private ChatConnection connection;
	private Member member;
	public DiscordChannelMemberModerationInterface(ChatConnection connection, Member member) {
		this.connection = connection;
		this.member = member;
	}
	
	@Override
	public Mono<Void> kick(String reason) {
		return member.kick(reason);
	}

	@Override
	public Mono<Void> ban(String reason) {
		return member.ban(
			BanQuerySpec.create()
				.withReason(reason));
	}

	@Override
	public Mono<Void> ban(String reason, int days) {
		return member.ban(
			BanQuerySpec.create()
				.withReason(reason)
				.withDeleteMessageDays(days));
	}
	
	@Override
	public ChatConnection getConnection() {
		return connection;
	}
	
	@Override
	public ChatClient getClient() {
		return getConnection().getClient();
	}
	
}
