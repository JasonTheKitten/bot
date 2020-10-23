package everyos.bot.luwu.run.command.modules.music;

import everyos.bot.chat4j.functionality.channel.ChatChannelTextInterface;
import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.CommandData;
import reactor.core.publisher.Mono;

public class MusicStopCommand extends GenericMusicCommand {
	@Override Mono<Void> execute(CommandData data, ArgumentParser parser, MusicManager manager) {
		manager.stop();
		return data.getChannel().getInterface(ChatChannelTextInterface.class)
			.send(data.getLocale().localize("command.music.stopped")).then();
	}

	@Override boolean requiresDJ() {
		return true;
	}
}
