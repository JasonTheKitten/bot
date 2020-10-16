package everyos.bot.luwu.run.command.modules.utility;

import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.Command;
import everyos.bot.luwu.core.command.CommandData;
import everyos.bot.luwu.core.entity.Locale;
import everyos.bot.luwu.core.entity.Member;
import reactor.core.publisher.Mono;

public class GhostCommand implements Command {
	@Override public Mono<Void> execute(CommandData data, ArgumentParser parser) {
		return
			checkPerms(data.getInvoker(), data.getLocale())
			.then(data.getMessage().delete());
	}

	private Mono<?> checkPerms(Member invoker, Locale locale) {
		return Mono.empty();
	}
}
