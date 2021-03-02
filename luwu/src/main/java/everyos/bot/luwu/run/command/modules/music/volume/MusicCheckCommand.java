package everyos.bot.luwu.run.command.modules.music.volume;

import everyos.bot.chat4j.entity.ChatPermission;
import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.CommandData;
import everyos.bot.luwu.core.entity.Channel;
import everyos.bot.luwu.core.entity.Locale;
import everyos.bot.luwu.core.functionality.channel.ChannelTextInterface;
import everyos.bot.luwu.run.command.modules.music.GenericMusicCommand;
import everyos.bot.luwu.run.command.modules.music.MusicManager;
import reactor.core.publisher.Mono;

public class MusicCheckCommand extends GenericMusicCommand {
	public MusicCheckCommand() {
		super("command.music.volume.check", e->true,
			ChatPermission.SEND_MESSAGES|ChatPermission.VC_CONNECT|ChatPermission.VC_SPEAK,
			ChatPermission.NONE);
	}

	@Override
	public Mono<Void> execute(CommandData data, ArgumentParser parser, MusicManager manager) {
		Locale locale = data.getLocale();
		
		return
			runCommand(data.getChannel(), manager, locale);
	}

	private Mono<Void> runCommand(Channel channel, MusicManager manager, Locale locale) {
		return Mono.defer(()->{
			int volume = manager.getVolume();
			
			return channel.getInterface(ChannelTextInterface.class).send(
				locale.localize("command.music.volume.check.message", "vol", String.valueOf(volume)));
		}).then();
	}

	@Override
	protected boolean requiresDJ() {
		return true;
	}
}
