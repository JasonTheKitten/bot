package everyos.bot.luwu.run.command.modules.music;

import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.CommandData;
import reactor.core.publisher.Mono;

public class MusicSwapCommand extends GenericMusicCommand {
	public MusicSwapCommand() {
		super("command.music.swap");
	}

	@Override
	Mono<Void> execute(CommandData data, ArgumentParser parser, MusicManager manager) {
		return Mono.empty();
	}

	@Override
	boolean requiresDJ() {
		return true;
	}
}
