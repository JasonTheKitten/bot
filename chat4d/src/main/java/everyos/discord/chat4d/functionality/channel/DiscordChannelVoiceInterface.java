package everyos.discord.chat4d.functionality.channel;

import java.nio.ByteBuffer;

import discord4j.core.object.entity.channel.VoiceChannel;
import discord4j.voice.AudioProvider;
import everyos.bot.chat4j.ChatClient;
import everyos.bot.chat4j.ChatConnection;
import everyos.bot.chat4j.audio.AudioBridge;
import everyos.bot.chat4j.entity.ChatVoiceConnection;
import everyos.bot.chat4j.functionality.channel.ChatChannelVoiceInterface;
import everyos.discord.chat4d.entity.DiscordVoiceConnection;
import reactor.core.publisher.Mono;

public class DiscordChannelVoiceInterface implements ChatChannelVoiceInterface {
	private VoiceChannel voiceChannel;
	private ChatConnection connection;

	public DiscordChannelVoiceInterface(ChatConnection connection, VoiceChannel voiceChannel) {
		this.connection = connection;
		this.voiceChannel = voiceChannel;
	}

	@Override
	public Mono<ChatVoiceConnection> join(AudioBridge bridge) {
		return voiceChannel.join(spec->{
			spec.setSelfDeaf(true);
			spec.setProvider(new AudioProvider() {
				@Override
				public boolean provide() {
					return bridge.provide();
				}
				
				@Override
				public ByteBuffer getBuffer() {
					return bridge.getBuffer();
				}
			});
		})
		.map(voiceConnection->new DiscordVoiceConnection(voiceConnection));
	}

	@Override
	public ChatConnection getConnection() {
		return connection;
	}

	@Override
	public ChatClient getClient() {
		return connection.getClient();
	}
}
