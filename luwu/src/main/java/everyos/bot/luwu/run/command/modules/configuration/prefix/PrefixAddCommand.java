package everyos.bot.luwu.run.command.modules.configuration.prefix;

import everyos.bot.chat4j.entity.ChatPermission;
import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.CommandData;
import everyos.bot.luwu.core.entity.Channel;
import everyos.bot.luwu.core.entity.Locale;
import everyos.bot.luwu.core.exception.TextException;
import everyos.bot.luwu.core.functionality.channel.ChannelTextInterface;
import everyos.bot.luwu.run.command.CommandBase;
import reactor.core.publisher.Mono;

public class PrefixAddCommand extends CommandBase {
	private boolean remove;

	public PrefixAddCommand(boolean remove) {
		super(remove?"command.prefix.remove":"command.prefix.add", e->true,
			ChatPermission.SEND_MESSAGES, ChatPermission.MANAGE_CHANNELS);
		this.remove = remove;
	}

	@Override
	public Mono<Void> execute(CommandData data, ArgumentParser parser) {
		Locale locale = data.getLocale();
		
		return parseArgs(parser, locale)
			.flatMap(prefix->runCommand(data.getChannel(), prefix, locale));
	}
	
	private Mono<String> parseArgs(ArgumentParser parser, Locale locale) {
		if (!parser.couldBeQuote()) return expect(locale, parser, "command.error.quote");
		
		return Mono.just(parser.eatQuote().stripLeading()); //TODO: Define an arbitrary prefix maximum length
	}

	private Mono<Void> runCommand(Channel channel, String prefix, Locale locale) {
		return channel.getServer()
			.flatMap(server->server.as(PrefixServer.type))
			.flatMap(server->server.edit(spec->{
				if (remove) {
					String similar = null;
					for (String cprefix: spec.getInfo().getPrefixes()) {
						if (cprefix.strip().equals(prefix.strip())) {
							similar = cprefix;
						}
					}
					if (similar==null) {
						return Mono.error(new TextException(locale.localize("command.prefix.remove.nosuchprefix")));
					}
					spec.removePrefix(similar);
				} else {
					if (prefix.isEmpty()) {
						return Mono.error(new TextException(locale.localize("command.prefix.remove.emptyprefix")));
					}
					spec.addPrefix(prefix);
				}
				return Mono.empty();
			}))
			.then(channel.getInterface(ChannelTextInterface.class).send(locale.localize(remove?
				"command.prefix.remove.removed":
				"command.prefix.add.added")))
			.then();
	}
}
