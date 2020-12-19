package everyos.bot.luwu.core.entity;

import everyos.bot.chat4j.audio.AudioBridge;
import everyos.bot.chat4j.functionality.channel.ChatChannelVoiceInterface;
import everyos.bot.luwu.core.functionality.channel.ChannelVoiceInterface;
import reactor.core.publisher.Mono;

public class ChannelVoiceInterfaceImp implements ChannelVoiceInterface {
	private final Channel channel;
	private final ChatChannelVoiceInterface voiceInterface;

	public ChannelVoiceInterfaceImp(Channel channel) {
		this.channel = channel;
		this.voiceInterface = channel.getRaw().getInterface(ChatChannelVoiceInterface.class);
	}

	@Override
	public Connection getConnection() {
		return channel.getConnection();
	}

	@Override
	public Client getClient() {
		return channel.getClient();
	}

	@Override
	public Mono<VoiceConnection> connect(AudioBridge bridge) {
		return voiceInterface.join(bridge)
			.map(connection->new VoiceConnection() {
				@Override
				public Mono<Void> leave() {
					return connection.disconnect();
				}
			});
	}
}
