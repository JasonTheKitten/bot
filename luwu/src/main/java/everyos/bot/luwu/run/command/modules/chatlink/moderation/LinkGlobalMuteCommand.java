package everyos.bot.luwu.run.command.modules.chatlink.moderation;

import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.CommandData;
import everyos.bot.luwu.run.command.CommandBase;
import reactor.core.publisher.Mono;

public class LinkGlobalMuteCommand extends CommandBase {
	public LinkGlobalMuteCommand() {
		super("command.link.mute.global");
	}

	@Override public Mono<Void> execute(CommandData data, ArgumentParser parser) {
		// TODO Auto-generated method stub
		return null;
	}
}
