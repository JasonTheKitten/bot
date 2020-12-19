package everyos.discord.chat4d.entity;

import discord4j.voice.VoiceConnection;
import everyos.bot.chat4j.entity.ChatVoiceConnection;
import reactor.core.publisher.Mono;

public class DiscordVoiceConnection implements ChatVoiceConnection {
	private VoiceConnection voiceConnection;

	public DiscordVoiceConnection(VoiceConnection voiceConnection) {
		this.voiceConnection = voiceConnection;
	}

	@Override
	public Mono<Void> disconnect() {
		return voiceConnection.disconnect();
	}
}
