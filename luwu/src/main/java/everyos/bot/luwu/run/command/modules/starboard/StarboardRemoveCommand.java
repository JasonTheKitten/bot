package everyos.bot.luwu.run.command.modules.starboard;

import everyos.bot.chat4j.entity.ChatPermission;
import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.CommandData;
import everyos.bot.luwu.core.entity.Channel;
import everyos.bot.luwu.core.entity.Locale;
import everyos.bot.luwu.core.functionality.channel.ChannelTextInterface;
import everyos.bot.luwu.run.command.CommandBase;
import reactor.core.publisher.Mono;

public class StarboardRemoveCommand extends CommandBase {
	public StarboardRemoveCommand() {
		super("command.starboard.remove", e->true,
			ChatPermission.SEND_EMBEDS,
			ChatPermission.MANAGE_CHANNELS);
	}

	@Override
	public Mono<Void> execute(CommandData data, ArgumentParser parser) {
		Locale locale = data.getLocale();
		
		return
			parseArgs(parser, locale)
			.flatMap(level->runCommand(data.getChannel(), level, locale))
			.then();
	}

	private Mono<Integer> parseArgs(ArgumentParser parser, Locale locale) {
		if (!parser.isNumerical()||Integer.valueOf(parser.peek())<=0) {
			return expect(locale, parser, "command.error.positiveinteger");
		}
		int level = (int) parser.eatNumerical();
		
		return Mono.just(level);
	}
	
	private Mono<Void> runCommand(Channel channel, int level, Locale locale) {
		return channel.getServer()
			.flatMap(s->s.as(StarboardServer.type))
			.flatMap(c->c.edit(spec->{
				spec.removeEmojiLevel(level);
			}))
			.then(channel.getInterface(ChannelTextInterface.class).send(locale.localize("command.starboard.remove.message")))
			.then();
		
		//TODO: What if 0 emojis remain?
				
	}
}
