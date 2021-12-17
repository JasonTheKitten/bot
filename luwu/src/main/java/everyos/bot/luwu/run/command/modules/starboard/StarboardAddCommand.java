package everyos.bot.luwu.run.command.modules.starboard;

import everyos.bot.chat4j.entity.ChatPermission;
import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.CommandData;
import everyos.bot.luwu.core.entity.Channel;
import everyos.bot.luwu.core.entity.EmojiID;
import everyos.bot.luwu.core.entity.Locale;
import everyos.bot.luwu.core.functionality.channel.ChannelTextInterface;
import everyos.bot.luwu.run.command.CommandBase;
import everyos.bot.luwu.util.Tuple;
import reactor.core.publisher.Mono;

public class StarboardAddCommand extends CommandBase {
	
	public StarboardAddCommand() {
		super("command.starboard.add", e -> true,
			ChatPermission.SEND_EMBEDS,
			ChatPermission.MANAGE_CHANNELS);
	}

	@Override
	public Mono<Void> execute(CommandData data, ArgumentParser parser) {
		Locale locale = data.getLocale();
		
		//TODO: Check that the starboard is actually enabled
		
		return
			parseArgs(parser, locale)
			.flatMap(tup -> runCommand(data.getChannel(), tup.getT1(), tup.getT2(), locale))
			.then();
	}

	private Mono<Tuple<Integer, EmojiID>> parseArgs(ArgumentParser parser, Locale locale) {
		if (!parser.isNumerical() || Integer.valueOf(parser.peek()) <= 0) {
			return expect(locale, parser, "command.error.positiveinteger");
		}
		int level = (int) parser.eatNumerical();
		
		if (!parser.couldBeEmojiID()) {
			return expect(locale, parser, "command.error.emoji");
		}
		EmojiID emojiID = parser.eatEmojiID();
		
		return Mono.just(Tuple.of(level, emojiID));
	}
	
	private Mono<Void> runCommand(Channel channel, int level, EmojiID emoji, Locale locale) {
		return channel.getServer()
			.flatMap(s->s.as(StarboardServer.type))
			.flatMap(c->c.edit(spec -> spec.addEmojiLevel(level, emoji)))
			.then(channel.getInterface(ChannelTextInterface.class).send(locale.localize("command.starboard.add.message")))
			.then();
				
	}
	
}
