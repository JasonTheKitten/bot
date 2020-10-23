package everyos.bot.luwu.run.command.modules.chatlink.moderation;

import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.Command;
import everyos.bot.luwu.core.command.CommandData;
import reactor.core.publisher.Mono;

public class LinkAcceptCommand implements Command {
	private static LinkAcceptCommand instance;

	@Override public Mono<Void> execute(CommandData data, ArgumentParser parser) {
		return Mono.empty(); //TODO
	}
	
	static {
		instance = new LinkAcceptCommand();
	}
	
	public static Command get() {
		return instance;
	}
}
