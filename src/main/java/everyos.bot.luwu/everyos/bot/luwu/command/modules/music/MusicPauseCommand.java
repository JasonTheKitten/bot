package everyos.bot.luwu.command.modules.music;

import everyos.bot.luwu.command.Command;
import everyos.bot.luwu.command.CommandData;
import everyos.bot.luwu.parser.ArgumentParser;
import reactor.core.publisher.Mono;

public class MusicPauseCommand implements Command {
	public MusicPauseCommand(boolean b) {
		
	}

	@Override public Mono<?> execute(CommandData data, ArgumentParser parser) {
		return null;
	}
}