package everyos.bot.luwu.run.command.modules.starboard;

import everyos.bot.chat4j.entity.ChatPermission;
import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.CommandData;
import everyos.bot.luwu.core.entity.Channel;
import everyos.bot.luwu.core.entity.ChannelID;
import everyos.bot.luwu.core.entity.Locale;
import everyos.bot.luwu.core.functionality.channel.ChannelTextInterface;
import everyos.bot.luwu.run.command.CommandBase;
import everyos.bot.luwu.util.Tuple;
import reactor.core.publisher.Mono;

public class StarboardOverrideCommand extends CommandBase {
	public StarboardOverrideCommand() {
		super("command.starboard.override", e->true,
			ChatPermission.SEND_MESSAGES,
			ChatPermission.MANAGE_CHANNELS);
	}

	@Override
	public Mono<Void> execute(CommandData data, ArgumentParser parser) {
		Locale locale = data.getLocale();
		
		return
			parseArgs(parser, locale)
			.flatMap(tup->runCommand(data.getChannel(), tup.getT1(), tup.getT2(), locale))
			.then();
	}

	private Mono<Tuple<ChannelID, Integer>> parseArgs(ArgumentParser parser, Locale locale) {
		if (!parser.couldBeChannelID()) {
			return expect(locale, parser, "command.error.channelid");
		}
		
		ChannelID channelID = parser.eatUncheckedChannelID();
		
		if (parser.isEmpty()) {
			return Mono.just(Tuple.of(channelID, -1));
		}
		
		if (!parser.isNumerical()||Integer.valueOf(parser.peek())<=0) {
			return expect(locale, parser, "command.error.positiveinteger");
		}
		int level = (int) parser.eatNumerical();
		
		return Mono.just(Tuple.of(channelID, level));
	}
	
	private Mono<Void> runCommand(Channel channel, ChannelID channelID, int level, Locale locale) {
		return channel.getServer()
			.flatMap(s->s.as(StarboardServer.type))
			.flatMap(c->c.edit(spec->{
				spec.addChannelOverride(channelID, level);
			}))
			.then(channel.getInterface(ChannelTextInterface.class).send("command.starboard.override.message"))
			.then();
	}
}
