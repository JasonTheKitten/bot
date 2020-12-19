package everyos.bot.chat4j.functionality.channel;

import everyos.bot.chat4j.audio.AudioBridge;
import everyos.bot.chat4j.entity.ChatVoiceConnection;
import everyos.bot.chat4j.functionality.ChatInterface;
import reactor.core.publisher.Mono;

public interface ChatChannelVoiceInterface extends ChatInterface {
	Mono<ChatVoiceConnection> join(AudioBridge bridge);
}
