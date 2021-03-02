package everyos.bot.luwu.run.command.modules.music;

import everyos.bot.chat4j.entity.ChatPermission;
import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.CommandData;
import everyos.bot.luwu.core.functionality.channel.ChannelTextInterface;
import reactor.core.publisher.Mono;

public class MusicRestartCommand extends GenericMusicCommand {
	public MusicRestartCommand() {
		super("command.music.restart", e->true, ChatPermission.SEND_MESSAGES, ChatPermission.NONE);
	}

	@Override
	protected Mono<Void> execute(CommandData data, ArgumentParser parser, MusicManager manager) {
		manager.getPlayingAudio().setPosition(0);
		manager.unpause();
		
		ChannelTextInterface channel = data.getChannel().getInterface(ChannelTextInterface.class);
		return channel.send(data.getLocale().localize("command.music.restarted"))
			.then();
	}

	@Override
	protected boolean requiresDJ() {
		return true;
	}
}
