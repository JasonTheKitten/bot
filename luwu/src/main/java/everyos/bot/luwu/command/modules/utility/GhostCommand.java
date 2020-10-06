package everyos.bot.luwu.command.modules.utility;

import everyos.bot.luwu.command.Command;
import everyos.bot.luwu.command.CommandData;
import everyos.bot.luwu.entity.Locale;
import everyos.bot.luwu.entity.Member;
import everyos.bot.luwu.parser.ArgumentParser;
import reactor.core.publisher.Mono;

public class GhostCommand implements Command {
	@Override public Mono<?> execute(CommandData data, ArgumentParser parser) {
		return
			checkPerms(data.getInvoker(), data.getLocale())
			.then(data.getMessage().delete());
	}

	private Mono<?> checkPerms(Member invoker, Locale locale) {
		return Mono.empty();
	}
}
