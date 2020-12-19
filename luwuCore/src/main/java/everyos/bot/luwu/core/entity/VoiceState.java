package everyos.bot.luwu.core.entity;

import everyos.bot.chat4j.entity.ChatVoiceState;
import reactor.core.publisher.Mono;

public class VoiceState {
	private Connection connection;
	private ChatVoiceState state;

	public VoiceState(Connection connection, ChatVoiceState state) {
		this.connection = connection;
		this.state = state;
	}

	public Mono<Channel> getChannel() {
		return state.getChannel()
			.flatMap(channel->Channel.getChannel(connection, channel));
	}
}
