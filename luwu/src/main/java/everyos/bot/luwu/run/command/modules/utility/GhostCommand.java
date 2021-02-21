package everyos.bot.luwu.run.command.modules.utility;

import everyos.bot.chat4j.entity.ChatPermission;
import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.CommandData;
import everyos.bot.luwu.run.command.CommandBase;
import reactor.core.publisher.Mono;

public class GhostCommand extends CommandBase {
	public GhostCommand() {
		super("command.ghost", e->true, ChatPermission.MANAGE_MESSAGES, ChatPermission.MANAGE_ROLES);
	}

	@Override public Mono<Void> execute(CommandData data, ArgumentParser parser) {
		return
			data.getMessage().delete();
	}
}
