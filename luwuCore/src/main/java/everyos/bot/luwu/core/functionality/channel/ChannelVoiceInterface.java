package everyos.bot.luwu.core.functionality.channel;

import everyos.bot.chat4j.audio.AudioBridge;
import everyos.bot.luwu.core.entity.VoiceConnection;
import everyos.bot.luwu.core.functionality.Interface;
import reactor.core.publisher.Mono;

public interface ChannelVoiceInterface extends Interface {
	Mono<VoiceConnection> connect(AudioBridge audioBridge);
}
