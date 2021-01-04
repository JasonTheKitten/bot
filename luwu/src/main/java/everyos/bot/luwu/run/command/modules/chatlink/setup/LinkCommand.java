package everyos.bot.luwu.run.command.modules.chatlink.setup;

import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.CommandData;
import everyos.bot.luwu.run.command.CommandBase;
import reactor.core.publisher.Mono;

public class LinkCommand extends CommandBase {
	public LinkCommand() {
		super("command.link.setup");
	}

	@Override public Mono<Void> execute(CommandData data, ArgumentParser parser) {
		return Mono.empty();
	}
}
