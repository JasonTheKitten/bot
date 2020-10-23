package everyos.bot.luwu.run.command.modules.music;

import everyos.bot.chat4j.functionality.channel.ChatChannelTextInterface;
import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.CommandData;
import reactor.core.publisher.Mono;

public class MusicRepeatCommand extends GenericMusicCommand {
	@Override Mono<Void> execute(CommandData data, ArgumentParser parser, MusicManager manager) {
		manager.setRepeat(!manager.getRepeat());
		ChatChannelTextInterface channel = data.getChannel().getInterface(ChatChannelTextInterface.class);
		return channel.send(data.getLocale().localize(
			"command.music.repeat."+
			(manager.getRepeat()?"on":"off")))
			.then();
	}

	@Override boolean requiresDJ() {
		return true;
	}
}
