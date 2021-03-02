package everyos.bot.luwu.run.command.modules.music;

import everyos.bot.chat4j.entity.ChatPermission;
import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.CommandData;
import everyos.bot.luwu.core.functionality.channel.ChannelTextInterface;
import reactor.core.publisher.Mono;

public class MusicRepeatCommand extends GenericMusicCommand {
	public MusicRepeatCommand() {
		super("command.music.repeat", e->true, ChatPermission.SEND_MESSAGES, ChatPermission.NONE);
	}

	@Override
	public Mono<Void> execute(CommandData data, ArgumentParser parser, MusicManager manager) {
		manager.setRepeat(!manager.getRepeat());
		ChannelTextInterface channel = data.getChannel().getInterface(ChannelTextInterface.class);
		return channel.send(data.getLocale().localize(
			"command.music.repeat."+
			(manager.getRepeat()?"on":"off")))
			.then();
	}

	@Override
	protected boolean requiresDJ() {
		return true;
	}
}
