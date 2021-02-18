package everyos.bot.luwu.run.command;

import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.Command;
import everyos.bot.luwu.core.command.CommandData;
import everyos.bot.luwu.core.entity.Locale;
import everyos.bot.luwu.core.exception.TextException;
import reactor.core.publisher.Mono;

public abstract class CommandBase implements Command {
	private final String id;
	
	@SuppressWarnings("unused")
	private RateLimit rateLimit;
	//TODO: Permissions scripts

	public CommandBase(String id) {
		this.id = id;
	}
	
	@Override
	public Mono<Void> run(CommandData data, ArgumentParser parser) {
		return execute(data, parser);
	}
	
	@Override
	public String getID() {
		return this.id;
	}
	
	protected <T> Mono<T> expect(Locale locale, ArgumentParser parser, String error) {
		String got = parser.getRemaining();
		if (got.isEmpty()) {
			got = locale.localize("command.error.nothing");
		}
		return Mono.error(new TextException(locale.localize("command.error.usage",
			"expected", locale.localize(error),
			"got", got)));
	}
}
