package everyos.bot.luwu.run.command.modules.info;

import everyos.bot.chat4j.entity.ChatPermission;
import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.CommandData;
import everyos.bot.luwu.run.command.CommandBase;
import reactor.core.publisher.Mono;

public class WebsiteCommand extends CommandBase {
	public WebsiteCommand() {
		super("command.website", e->true, ChatPermission.SEND_MESSAGES, ChatPermission.NONE);
	}

	@Override
	public Mono<Void> execute(CommandData data, ArgumentParser parser) {
		//data.getBotEngine().getInfo();
		return Mono.empty();
	}
}
