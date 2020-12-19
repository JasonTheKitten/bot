package everyos.bot.luwu.core.functionality.member;

import everyos.bot.luwu.core.entity.VoiceState;
import everyos.bot.luwu.core.functionality.Interface;
import reactor.core.publisher.Mono;

public interface MemberVoiceConnectionInterface extends Interface {
	public Mono<VoiceState> getVoiceState();
}
