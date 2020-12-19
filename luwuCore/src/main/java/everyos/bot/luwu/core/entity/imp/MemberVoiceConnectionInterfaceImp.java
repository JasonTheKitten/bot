package everyos.bot.luwu.core.entity.imp;

import everyos.bot.chat4j.functionality.member.ChatMemberVoiceConnectionInterface;
import everyos.bot.luwu.core.entity.Client;
import everyos.bot.luwu.core.entity.Connection;
import everyos.bot.luwu.core.entity.VoiceState;
import everyos.bot.luwu.core.functionality.member.MemberVoiceConnectionInterface;
import reactor.core.publisher.Mono;

public class MemberVoiceConnectionInterfaceImp implements MemberVoiceConnectionInterface {
	private Connection connection;
	private ChatMemberVoiceConnectionInterface voiceConnection;

	public MemberVoiceConnectionInterfaceImp(Connection connection, ChatMemberVoiceConnectionInterface voiceConnection) {
		this.connection = connection;
		this.voiceConnection = voiceConnection;
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
	public Mono<VoiceState> getVoiceState() {
		return voiceConnection.getVoiceState()
			.map(state->new VoiceState(connection, state));
	}
}
