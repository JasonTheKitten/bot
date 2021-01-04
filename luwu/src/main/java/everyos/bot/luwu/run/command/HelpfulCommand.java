package everyos.bot.luwu.run.command;

import everyos.bot.luwu.core.command.Command;
import everyos.bot.luwu.core.entity.Locale;
import reactor.core.publisher.Mono;

public abstract class HelpfulCommand implements Command {
	public Mono<Void> displayCorrectUsage(Locale locale, String unlocalizedExpected, String got) {
		return Mono.empty();
		//TODO
	}
	public Mono<Void> displayInfo(Locale locale) {
		return displayCorrectUsage(locale, null, null);
	}
}
