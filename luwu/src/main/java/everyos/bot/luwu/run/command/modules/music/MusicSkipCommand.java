package everyos.bot.luwu.run.command.modules.music;

import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.CommandData;
import everyos.bot.luwu.core.entity.Locale;
import everyos.bot.luwu.core.exception.TextException;
import everyos.bot.luwu.core.functionality.channel.ChannelTextInterface;
import reactor.core.publisher.Mono;

public class MusicSkipCommand extends GenericMusicCommand {
	public MusicSkipCommand() {
		super("command.music.skip");
	}

	@Override
	public Mono<Void> execute(CommandData data, ArgumentParser parser, MusicManager manager) {
		Locale locale = data.getLocale();
		
		int trackpos = 0;
		if (!parser.isEmpty()) {
			if (!parser.isNumerical()) {
				return Mono.error(new TextException(locale.localize("command.error.arguments",
					"expected", locale.localize("command.error.positiveinteger"),
					"got", parser.toString())));
			}
			trackpos = (int) parser.eatNumerical();
		}
		if (trackpos==0) {
			manager.playNext();
		} else {
			//manager.
		}
		return data.getChannel().getInterface(ChannelTextInterface.class)
			.send(locale.localize("command.music.skipped")).then();
	}

	@Override
	boolean requiresDJ() {
		return true;
	}
}
