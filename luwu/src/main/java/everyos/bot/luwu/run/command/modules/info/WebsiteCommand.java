package everyos.bot.luwu.run.command.modules.info;

import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.CommandData;
import everyos.bot.luwu.run.command.CommandBase;
import reactor.core.publisher.Mono;

public class WebsiteCommand extends CommandBase {
	public WebsiteCommand() {
		super("command.website");
	}

	@Override
	public Mono<Void> execute(CommandData data, ArgumentParser parser) {
		//data.getBotEngine().getInfo();
		return Mono.empty();
	}
}
