package everyos.bot.luwu.run.command.modules.music;

import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.CommandData;
import everyos.bot.luwu.run.command.CommandBase;
import reactor.core.publisher.Mono;

public class MusicNowPlayingCommand extends CommandBase {
	public MusicNowPlayingCommand() {
		super("command.music.nowplaying");
	}

	@Override public Mono<Void> execute(CommandData data, ArgumentParser parser) {
		return null;
	}
}
