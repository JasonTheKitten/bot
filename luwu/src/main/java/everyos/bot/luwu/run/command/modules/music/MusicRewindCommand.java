package everyos.bot.luwu.run.command.modules.music;

import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.CommandData;
import reactor.core.publisher.Mono;

public class MusicRewindCommand extends GenericMusicCommand {
	public MusicRewindCommand() {
		super("command.music.rewind");
	}

	@Override
	Mono<Void> execute(CommandData data, ArgumentParser parser, MusicManager manager) {
		return null;
	}

	@Override
	boolean requiresDJ() {
		return true;
	}
}
