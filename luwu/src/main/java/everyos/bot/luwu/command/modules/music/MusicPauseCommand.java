package everyos.bot.luwu.command.modules.music;

import everyos.bot.luwu.command.CommandData;
import everyos.bot.luwu.parser.ArgumentParser;
import reactor.core.publisher.Mono;

public class MusicPauseCommand extends GenericMusicCommand {
	private boolean doPause;

	public MusicPauseCommand(boolean b) {
		this.doPause = b;
	}

	@Override Mono<?> execute(CommandData data, ArgumentParser parser, MusicManager manager) {
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
