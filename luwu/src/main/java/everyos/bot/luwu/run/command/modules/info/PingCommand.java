package everyos.bot.luwu.run.command.modules.info;

import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.CommandData;
import everyos.bot.luwu.run.command.CommandBase;
import reactor.core.publisher.Mono;

public class PingCommand extends CommandBase {
	public PingCommand() {
		super("command.ping");
	}

	@Override
	public Mono<Void> execute(CommandData data, ArgumentParser parser) {
		// TODO Auto-generated method stub
		return Mono.empty();
	}
}
