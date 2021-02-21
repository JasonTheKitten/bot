package everyos.discord.chat4d.functionality.member;

import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Member;
import everyos.bot.chat4j.ChatClient;
import everyos.bot.chat4j.ChatConnection;
import everyos.bot.chat4j.functionality.member.ChatMemberRoleInterface;
import reactor.core.publisher.Mono;

public class DiscordGuildMemberRoleInterface implements ChatMemberRoleInterface {
	
	private ChatConnection connection;
	private Member member;

	public DiscordGuildMemberRoleInterface(ChatConnection connection, Member member) {
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
	public Mono<Void> addRole(long role, String reason) {
		return member.addRole(Snowflake.of(role), reason);
	}

	@Override
	public Mono<Void> removeRole(long role, String reason) {
		return member.removeRole(Snowflake.of(role), reason);
	}

}
