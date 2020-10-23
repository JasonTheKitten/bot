package everyos.bot.luwu.run.command.modules.fun;

import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.Command;
import everyos.bot.luwu.core.command.CommandData;
import reactor.core.publisher.Mono;

public class GiphyCommand implements Command {
	@Override public Mono<Void> execute(CommandData data, ArgumentParser parser) {
		return Mono.empty();
	}
}
