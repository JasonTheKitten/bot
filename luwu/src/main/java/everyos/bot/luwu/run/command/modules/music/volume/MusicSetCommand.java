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

public class MusicSetCommand extends GenericMusicCommand {
	public MusicSetCommand() {
		super("command.music.volume.set", e->true,
			ChatPermission.SEND_MESSAGES|ChatPermission.VC_CONNECT|ChatPermission.VC_SPEAK,
			ChatPermission.NONE);
	}

	@Override
	public Mono<Void> execute(CommandData data, ArgumentParser parser, MusicManager manager) {
		Locale locale = data.getLocale();
		
		return parseArgs(parser, locale)
			.flatMap(slide->runCommand(data.getChannel(), manager, slide, locale));
	}

	private Mono<Integer> parseArgs(ArgumentParser parser, Locale locale) {
		if (!parser.isNumerical() || Integer.valueOf(parser.peek())<1) {
			return expect(locale, parser, "command.error.positiveinteger");
		}
		
		return Mono.just((int) parser.eatNumerical());
	}
	
	private Mono<Void> runCommand(Channel channel, MusicManager manager, int nvolume, Locale locale) {
		return Mono.defer(()->{
			int volume = nvolume;
			if (volume>200) volume = 200;
			if (volume<0) volume = 0;
			
			manager.setVolume(volume);
			
			return channel.getInterface(ChannelTextInterface.class).send(
				locale.localize("command.music.volume.set.newvol", "vol", String.valueOf(volume)));
		}).then();
	}

	@Override
	protected boolean requiresDJ() {
		return true;
	}
}
