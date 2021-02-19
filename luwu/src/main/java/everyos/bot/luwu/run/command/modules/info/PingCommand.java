package everyos.bot.luwu.run.command.modules.info;

import everyos.bot.chat4j.entity.ChatPermission;
import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.CommandData;
import everyos.bot.luwu.run.command.CommandBase;
import reactor.core.publisher.Mono;

public class PingCommand extends CommandBase {
	public PingCommand() {
		super("command.ping", e->true, ChatPermission.SEND_MESSAGES, ChatPermission.NONE);
	}

	@Override
	public Mono<Void> execute(CommandData data, ArgumentParser parser) {
		// TODO Auto-generated method stub
		return Mono.empty();
	}
}
