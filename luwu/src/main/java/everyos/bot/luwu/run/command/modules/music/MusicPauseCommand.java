package everyos.bot.luwu.run.command.modules.music;

import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.CommandData;
import everyos.bot.luwu.core.functionality.channel.ChannelTextInterface;
import reactor.core.publisher.Mono;

public class MusicPauseCommand extends GenericMusicCommand {
	private boolean doPause;

	public MusicPauseCommand(boolean b) {
		super(b?"command.music.paused":"command.music.unpaused");
		this.doPause = b;
	}

	@Override Mono<Void> execute(CommandData data, ArgumentParser parser, MusicManager manager) {
		if (doPause) {
			manager.pause();
			return data.getChannel().getInterface(ChannelTextInterface.class)
				.send(data.getLocale().localize("command.music.paused")).then();
		} else {
			manager.unpause();
			return data.getChannel().getInterface(ChannelTextInterface.class)
				.send(data.getLocale().localize("command.music.unpaused")).then();
		}
	}

	@Override boolean requiresDJ() {
		return true;
	}
}
