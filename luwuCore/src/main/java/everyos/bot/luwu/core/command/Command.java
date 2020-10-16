package everyos.bot.luwu.core.command;

import everyos.bot.luwu.core.client.ArgumentParser;
import reactor.core.publisher.Mono;

public interface Command {
	public Mono<Void> execute(CommandData data, ArgumentParser parser);
}
