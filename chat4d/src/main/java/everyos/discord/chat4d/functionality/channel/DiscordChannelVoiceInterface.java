package everyos.discord.chat4d.functionality.channel;

import java.nio.ByteBuffer;

import discord4j.core.object.entity.channel.VoiceChannel;
import discord4j.voice.AudioProvider;
import everyos.bot.chat4j.ChatConnection;
import everyos.bot.chat4j.audio.AudioBridge;
import everyos.bot.chat4j.entity.ChatVoiceConnection;
import everyos.bot.chat4j.functionality.channel.ChatChannelVoiceInterface;
import everyos.discord.chat4d.entity.DiscordVoiceConnection;
import everyos.discord.chat4d.event.DiscordEvent;
import reactor.core.publisher.Mono;

public class DiscordChannelVoiceInterface extends DiscordEvent implements ChatChannelVoiceInterface {
	private VoiceChannel voiceChannel;

	public DiscordChannelVoiceInterface(ChatConnection connection, VoiceChannel voiceChannel) {
		super(connection);
		
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
}
