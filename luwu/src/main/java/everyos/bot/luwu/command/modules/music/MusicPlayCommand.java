package everyos.bot.luwu.command.modules.music;

import everyos.bot.luwu.command.CommandData;
import everyos.bot.luwu.parser.ArgumentParser;
import reactor.core.publisher.Mono;

public class MusicPlayCommand extends GenericMusicCommand {
	@Override Mono<?> execute(CommandData data, ArgumentParser parser, MusicManager manager) {
		return null;
	}

	@Override boolean requiresDJ() {
		return true;
	}
}
