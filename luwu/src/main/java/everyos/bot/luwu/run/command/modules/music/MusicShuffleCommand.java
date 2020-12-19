package everyos.bot.luwu.run.command.modules.music;

import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.CommandData;
import reactor.core.publisher.Mono;

public class MusicShuffleCommand extends GenericMusicCommand {
	@Override
	Mono<Void> execute(CommandData data, ArgumentParser parser, MusicManager manager) {
		//manager.getQueue().
		return Mono.empty();
	}

	@Override
	boolean requiresDJ() {
		return true;
	}
}
