package everyos.bot.luwu.run.command.modules.music;

import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.CommandData;
import everyos.bot.luwu.run.command.CommandBase;
import reactor.core.publisher.Mono;

public class MusicQueueCommand extends CommandBase {
	public MusicQueueCommand() {
		super("command.music.queue");
	}

	@Override public Mono<Void> execute(CommandData data, ArgumentParser parser) {
		return null;
	}
}
