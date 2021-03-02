package everyos.bot.luwu.run.command.modules.starboard;

import everyos.bot.luwu.core.entity.event.ReactionEvent;
import reactor.core.publisher.Mono;

public class StaroardHooks {
	public Mono<Void> starboardHook(ReactionEvent event) {
		return event.getMessage()
			.flatMap(message->message.as(StarboardMessage.type))
			.flatMap(message->message.getInfo())
			.flatMap(info->info.isStarboardMessage()?Mono.just(info):processOriginalMessage(info))
			.flatMap(info->updateStarboardPost(info));
	}

	private Mono<MessageInfo> processOriginalMessage(MessageInfo info) {
		// TODO Auto-generated method stub
		return Mono.empty();
	}
	
	private Mono<Void> updateStarboardPost(MessageInfo info) {
		// TODO Auto-generated method stub
		return null;
	}
}
