package everyos.bot.luwu.run.command.modules.starboard;

import everyos.bot.chat4j.entity.ChatPermission;
import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.CommandData;
import everyos.bot.luwu.core.entity.ChannelID;
import everyos.bot.luwu.core.entity.EmojiID;
import everyos.bot.luwu.core.entity.Locale;
import everyos.bot.luwu.run.command.CommandBase;
import everyos.bot.luwu.util.Tuple;
import reactor.core.publisher.Mono;

public class StarboardSetCommand extends CommandBase {
	public StarboardSetCommand() {
		super("command.starboard.set", e->true,
			ChatPermission.SEND_EMBEDS,
			ChatPermission.MANAGE_CHANNELS);
	}

	@Override
	public Mono<Void> execute(CommandData data, ArgumentParser parser) {
		Locale locale = data.getLocale();
		
		return
			parseArgs(parser, locale)
			.then();
	}

	private Mono<Tuple<EmojiID, ChannelID>> parseArgs(ArgumentParser parser, Locale locale) {
		if (!parser.couldBeChannelID()) {
			return expect(locale, parser, "command.error.channelid");
		}
		ChannelID channelID = parser.eatChannelID();
		
		if (!parser.couldBeEmojiID()) {
			return expect(locale, parser, "command.error.emoji");
		}
		EmojiID emojiID = parser.eatEmojiID();
		
		return Mono.just(Tuple.of(emojiID, channelID));
	}
}
