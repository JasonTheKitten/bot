package everyos.bot.luwu.run.command.modules.utility;

import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.CommandData;
import everyos.bot.luwu.core.entity.Locale;
import everyos.bot.luwu.core.entity.Member;
import everyos.bot.luwu.run.command.CommandBase;
import reactor.core.publisher.Mono;

public class GhostCommand extends CommandBase {
	public GhostCommand() {
		super("command.ghost");
	}

	@Override public Mono<Void> execute(CommandData data, ArgumentParser parser) {
		return
			checkPerms(data.getInvoker(), data.getLocale())
			.then(data.getMessage().delete());
	}

	private Mono<?> checkPerms(Member invoker, Locale locale) {
		return Mono.empty(); //TODO
	}
}
