package everyos.discord.chat4d.functionality.member;

import discord4j.core.object.entity.Member;
import everyos.bot.chat4j.ChatClient;
import everyos.bot.chat4j.ChatConnection;
import everyos.bot.chat4j.entity.ChatVoiceState;
import everyos.bot.chat4j.functionality.member.ChatMemberVoiceConnectionInterface;
import everyos.discord.chat4d.entity.DiscordVoiceState;
import reactor.core.publisher.Mono;

public class DiscordGuildMemberVoiceConnectionInterface implements ChatMemberVoiceConnectionInterface {
	private ChatConnection connection;
	private Member member;

	public DiscordGuildMemberVoiceConnectionInterface(ChatConnection connection, Member member) {
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
	public Mono<ChatVoiceState> getVoiceState() {
		return member.getVoiceState()
			.map(voiceState->new DiscordVoiceState(connection, voiceState));
	}

}
