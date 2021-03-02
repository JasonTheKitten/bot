package everyos.bot.luwu.run.command.modules.music;

import everyos.bot.chat4j.entity.ChatPermission;
import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.CommandData;
import everyos.bot.luwu.core.functionality.channel.ChannelTextInterface;
import reactor.core.publisher.Mono;

public class MusicPauseCommand extends GenericMusicCommand {
	private boolean doPause;

	public MusicPauseCommand(boolean b) {
		super(b?"command.music.pause":"command.music.unpause", e->true, ChatPermission.SEND_MESSAGES, ChatPermission.NONE);
		this.doPause = b;
	}

	@Override
	protected Mono<Void> execute(CommandData data, ArgumentParser parser, MusicManager manager) {
		if (doPause) {
			manager.pause();
			return data.getChannel().getInterface(ChannelTextInterface.class)
				.send(data.getLocale().localize("command.music.pause.paused")).then();
		} else {
			manager.unpause();
			return data.getChannel().getInterface(ChannelTextInterface.class)
				.send(data.getLocale().localize("command.music.unpause.unpaused")).then();
		}
	}

	@Override
	protected boolean requiresDJ() {
		return true;
	}
}
