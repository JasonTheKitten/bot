package everyos.bot.luwu.run.command.modules.music;

import everyos.bot.chat4j.entity.ChatPermission;
import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.CommandData;
import everyos.bot.luwu.core.entity.Channel;
import everyos.bot.luwu.core.entity.Locale;
import everyos.bot.luwu.core.entity.User;
import reactor.core.publisher.Mono;

public class MusicPlayCommand extends GenericMusicCommand {
	public MusicPlayCommand() {
		super("command.music.play", e->true,
			ChatPermission.SEND_MESSAGES|ChatPermission.SEND_EMBEDS|ChatPermission.VC_CONNECT|ChatPermission.VC_SPEAK,
			ChatPermission.NONE);
	}

	@Override
	protected Mono<Void> execute(CommandData data, ArgumentParser parser, MusicManager manager) {
		Locale locale = data.getLocale();
		
		return
			parseArgs(parser, locale)
			.flatMap(query->runCommand(data.getChannel(), data.getInvoker(), query, manager, locale));
	}
	
	private Mono<String> parseArgs(ArgumentParser parser, Locale locales) {
		if (parser.isEmpty()) {
			return expect(locales, parser, "command.error.string");
		}
		return Mono.just(parser.getRemaining());
	}

	public Mono<Void> runCommand(Channel channel, User invoker, String query, MusicManager manager, Locale locale) {
		return MusicUtil.lookup(query).flatMap(track->{
			MusicTrack mtrack = new MusicTrack(track, invoker.getID());
			manager.getQueue().queue(mtrack);
			manager.ready();
			return MusicNowPlayingCommand.showPlaying(channel, manager, mtrack, locale);
		}).then();
	}

	@Override
	protected boolean requiresDJ() {
		return true;
	}
}
