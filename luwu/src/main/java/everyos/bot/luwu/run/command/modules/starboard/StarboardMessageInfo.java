package everyos.bot.luwu.run.command.modules.starboard;

import reactor.core.publisher.Mono;

public interface StarboardMessageInfo {
	Mono<StarboardMessage> getStarboardMessage();
	Mono<StarboardMessage> getOriginalMessage();
	boolean hasStarboardMessage();
	boolean isStarboardMessage();
	Mono<StarboardServer> getStarboardServer();
}
