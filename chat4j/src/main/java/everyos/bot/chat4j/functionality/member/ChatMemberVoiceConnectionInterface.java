package everyos.bot.chat4j.functionality.member;

import everyos.bot.chat4j.entity.ChatVoiceState;
import everyos.bot.chat4j.functionality.ChatInterface;
import reactor.core.publisher.Mono;

public interface ChatMemberVoiceConnectionInterface extends ChatInterface {
	Mono<ChatVoiceState> getVoiceState();
}
