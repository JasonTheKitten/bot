package everyos.bot.luwu.run.command.modules.info;

import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.Command;
import everyos.bot.luwu.core.command.CommandData;
import reactor.core.publisher.Mono;

public class WebsiteCommand implements Command {
	@Override
	public Mono<Void> execute(CommandData data, ArgumentParser parser) {
		//data.getBotEngine().getInfo();
		return Mono.empty();
	}
}
