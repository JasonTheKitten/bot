package everyos.bot.luwu.command;

import everyos.bot.luwu.parser.ArgumentParser;
import reactor.core.publisher.Mono;

public interface Command {
	public Mono<?> execute(CommandData data, ArgumentParser parser);
}
