package everyos.nertivia.chat4n.functionality.member;

import everyos.bot.chat4j.ChatClient;
import everyos.bot.chat4j.ChatConnection;
import everyos.bot.chat4j.functionality.member.ChatMemberModerationInterface;
import everyos.nertivia.nertivia4j.entity.Member;
import reactor.core.publisher.Mono;

public class NertiviaMemberModerationInterface implements ChatMemberModerationInterface {
	private ChatConnection connection;
	private Member member;

	public NertiviaMemberModerationInterface(ChatConnection connection, Member member) {
		this.connection = connection;
		this.member = member;
	}

	@Override
	public ChatConnection getConnection() {
		return connection;
	}

	@Override
	public ChatClient getClient() {
		return connection.getClient();
	}

	@Override
	public Mono<Void> kick(String reason) {
		return member.kick(reason);
	}

	@Override
	public Mono<Void> ban(String reason) {
		return member.ban(reason);
	}

	@Override
	public Mono<Void> ban(String reason, int days) {
		return member.ban(reason);
	}

}
