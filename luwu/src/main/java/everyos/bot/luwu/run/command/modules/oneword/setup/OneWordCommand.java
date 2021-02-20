package everyos.bot.luwu.run.command.modules.oneword.setup;

import everyos.bot.chat4j.entity.ChatPermission;
import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.CommandData;
import everyos.bot.luwu.core.entity.Channel;
import everyos.bot.luwu.core.entity.Locale;
import everyos.bot.luwu.core.functionality.channel.ChannelTextInterface;
import everyos.bot.luwu.run.command.CommandBase;
import everyos.bot.luwu.run.command.modules.oneword.OneWordChannel;
import reactor.core.publisher.Mono;

public class OneWordCommand extends CommandBase {
	public OneWordCommand() {
		super("command.oneword.setup", e->true,
			ChatPermission.SEND_EMBEDS|ChatPermission.MANAGE_CHANNELS,
			ChatPermission.MANAGE_CHANNELS);
	}

	@Override
	public Mono<Void> execute(CommandData data, ArgumentParser parser) {
		Locale locale = data.getLocale();
		
		return parseArgs(parser, locale)
			.flatMap(useCurrentChannel->createChannelIfDesired(!useCurrentChannel, data.getChannel()))
			.flatMap(channel->runCommand(channel, data.getChannel(), locale));
	}

	private Mono<Boolean> parseArgs(ArgumentParser parser, Locale locale) {
		if (!parser.isEmpty()) {
			if (parser.peek().equals("--current")||parser.peek().equals("-c")) {
				parser.eat();
				return Mono.just(true);
			} else {
				return expect(locale, parser, "command.error.nothing");
			}
		}
		return Mono.just(false);
	}
	
	private Mono<Channel> createChannelIfDesired(boolean createNewChannel, Channel channel) {
		return Mono.just(channel); //TODO
	}

	private Mono<Void> runCommand(Channel channel, Channel outputChannel, Locale locale) {
		return channel.as(OneWordChannel.type).flatMap(c->c.reset())
			.then(outputChannel.getInterface(ChannelTextInterface.class).send(
				locale.localize("command.oneword.setup.created", "channel", "<#"+channel.getID().toString()+">")))
			.then();
	}
}
