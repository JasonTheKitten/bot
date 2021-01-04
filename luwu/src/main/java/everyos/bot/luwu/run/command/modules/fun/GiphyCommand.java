package everyos.bot.luwu.run.command.modules.fun;

import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.CommandData;
import everyos.bot.luwu.run.command.CommandBase;
import reactor.core.publisher.Mono;

public class GiphyCommand extends CommandBase {
	public GiphyCommand(String id) {
		super("command.giphy");
	}

	@Override public Mono<Void> execute(CommandData data, ArgumentParser parser) {
		return Mono.empty();
	}
}
