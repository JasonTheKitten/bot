package everyos.bot.luwu.run.command.modules.music;

import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.CommandData;
import everyos.bot.luwu.core.functionality.channel.ChannelTextInterface;
import reactor.core.publisher.Mono;

public class MusicRestartCommand extends GenericMusicCommand {
	public MusicRestartCommand() {
		super("command.music.restart");
	}

	@Override public Mono<Void> execute(CommandData data, ArgumentParser parser, MusicManager manager) {
		manager.getPlayingAudio().setPosition(0);
		
		ChannelTextInterface channel = data.getChannel().getInterface(ChannelTextInterface.class);
		return channel.send(data.getLocale().localize("command.music.restarted"))
			.then();
	}

	@Override
	boolean requiresDJ() {
		return true;
	}
}
