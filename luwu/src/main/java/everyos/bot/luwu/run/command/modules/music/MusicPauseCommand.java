package everyos.bot.luwu.run.command.modules.music;

import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.CommandData;
import reactor.core.publisher.Mono;

public class MusicPauseCommand extends GenericMusicCommand {
	private boolean doPause;

	public MusicPauseCommand(boolean b) {
		this.doPause = b;
	}

	@Override Mono<Void> execute(CommandData data, ArgumentParser parser, MusicManager manager) {
		if (doPause) {
			manager.pause();
		} else {
			manager.unpause();
		}
		return Mono.empty(); //TODO
	}

	@Override boolean requiresDJ() {
		return true;
	}
}
