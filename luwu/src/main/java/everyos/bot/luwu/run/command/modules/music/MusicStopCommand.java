package everyos.bot.luwu.run.command.modules.music;

import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.CommandData;
import everyos.bot.luwu.core.functionality.channel.ChannelTextInterface;
import reactor.core.publisher.Mono;

public class MusicStopCommand extends GenericMusicCommand {
	@Override
	Mono<Void> execute(CommandData data, ArgumentParser parser, MusicManager manager) {
		manager.stop();
		return data.getChannel().getInterface(ChannelTextInterface.class)
			.send(data.getLocale().localize("command.music.stopped")).then();
	}

	@Override
	boolean requiresDJ() {
		return true;
	}
}
