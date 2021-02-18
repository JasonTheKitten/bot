package everyos.bot.luwu.core.entity.imp;

import everyos.bot.chat4j.functionality.member.ChatMemberModerationInterface;
import everyos.bot.luwu.core.entity.Client;
import everyos.bot.luwu.core.entity.Connection;
import everyos.bot.luwu.core.functionality.member.MemberModerationInterface;
import reactor.core.publisher.Mono;

public class MemberModerationInterfaceImp implements MemberModerationInterface {
	private Connection connection;
	private ChatMemberModerationInterface moderationInt;

	public MemberModerationInterfaceImp(Connection connection, ChatMemberModerationInterface moderationInt) {
		this.connection = connection;
		this.moderationInt = moderationInt;
	}

	@Override
	public Connection getConnection() {
		return connection;
	}

	@Override
	public Client getClient() {
		return connection.getClient();
	}

	@Override
	public Mono<Void> ban(String reason) {
		return moderationInt.ban(reason);
	}

	@Override
	public Mono<Void> kick(String reason) {
		return moderationInt.kick(reason);
	}

}
