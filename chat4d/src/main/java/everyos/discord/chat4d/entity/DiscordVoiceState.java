package everyos.discord.chat4d.entity;

import discord4j.core.object.VoiceState;
import everyos.bot.chat4j.ChatConnection;
import everyos.bot.chat4j.entity.ChatChannel;
import everyos.bot.chat4j.entity.ChatVoiceState;
import reactor.core.publisher.Mono;

public class DiscordVoiceState implements ChatVoiceState {
	private ChatConnection connection;
	private VoiceState voiceState;

	public DiscordVoiceState(ChatConnection connection, VoiceState voiceState) {
		this.connection = connection;
		this.voiceState = voiceState;
	}

	@Override
	public Mono<ChatChannel> getChannel() {
		return voiceState.getChannel()
			.map(channel->new DiscordChannel(connection, channel));
	}
}
