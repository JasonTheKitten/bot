package everyos.bot.luwu.run.command.modules.starboard;

import everyos.bot.chat4j.entity.ChatPermission;
import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.CommandData;
import everyos.bot.luwu.core.entity.Channel;
import everyos.bot.luwu.core.entity.ChannelID;
import everyos.bot.luwu.core.entity.EmojiID;
import everyos.bot.luwu.core.entity.Locale;
import everyos.bot.luwu.core.functionality.channel.ChannelTextInterface;
import everyos.bot.luwu.run.command.CommandBase;
import everyos.bot.luwu.util.Tuple;
import reactor.core.publisher.Mono;

public class StarboardSetCommand extends CommandBase {
	public StarboardSetCommand() {
		super("command.starboard.set", e->true,
			ChatPermission.SEND_MESSAGES|ChatPermission.SEND_EMBEDS,
			ChatPermission.MANAGE_CHANNELS);
	}

	@Override
	public Mono<Void> execute(CommandData data, ArgumentParser parser) {
		Locale locale = data.getLocale();
		
		return
			parseArgs(parser, data.getChannel(), locale)
			.flatMap(tup->runCommand(data.getChannel(), tup.getT1(), tup.getT2(), locale))
			.then();
	}

	private Mono<Tuple<EmojiID, ChannelID>> parseArgs(ArgumentParser parser, Channel baseChannel, Locale locale) {
		if (!parser.couldBeChannelID()) {
			return expect(locale, parser, "command.error.channelid");
		}
		
		return parser
			.eatChannel(baseChannel, locale)
			.flatMap(channel -> {
				if (!parser.couldBeEmojiID()) {
					return expect(locale, parser, "command.error.emoji");
				}
				EmojiID emojiID = parser.eatEmojiID();
				
				return Mono.just(Tuple.of(emojiID, channel.getID()));
			});
	}
	
	private Mono<Void> runCommand(Channel channel, EmojiID emoji, ChannelID channelID, Locale locale) {
		return channel.getServer()
			.flatMap(s->s.as(StarboardServer.type))
			.flatMap(c->c.edit(spec->{
				spec.setReaction(emoji);
				spec.setOutputChannel(channelID);
			}))
			.then(channel.getInterface(ChannelTextInterface.class)
				.send(locale.localize("command.starboard.set.message")))
			.then();
				
	}
}
