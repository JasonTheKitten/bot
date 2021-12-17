package everyos.bot.luwu.run.command.modules.suggestions;

import everyos.bot.chat4j.entity.ChatPermission;
import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.CommandData;
import everyos.bot.luwu.core.entity.Channel;
import everyos.bot.luwu.core.entity.ChannelID;
import everyos.bot.luwu.core.entity.Locale;
import everyos.bot.luwu.core.functionality.channel.ChannelTextInterface;
import everyos.bot.luwu.run.command.CommandBase;
import reactor.core.publisher.Mono;

public class SuggestionChannelCommand extends CommandBase {
	public SuggestionChannelCommand() {
		super("command.suggestion", e->true,
			ChatPermission.SEND_EMBEDS|ChatPermission.ADD_REACTIONS|ChatPermission.MANAGE_CHANNELS,
			ChatPermission.MANAGE_CHANNELS);
	}

	@Override
	public Mono<Void> execute(CommandData data, ArgumentParser parser) {
		Locale locale = data.getLocale();
		
		return parseArgs(parser, data.getChannel(), locale)
			.switchIfEmpty(Mono.just(data.getChannel().getID()))
			.flatMap(oc->runCommand(data.getChannel(), oc, locale))
			.then(sendMessage(data.getChannel(), locale));
	}

	private Mono<Void> sendMessage(Channel channel, Locale locale) {
		return
			channel.getInterface(ChannelTextInterface.class).send(locale.localize("command.suggestion.message"))
			.then();
	}

	private Mono<ChannelID> parseArgs(ArgumentParser parser, Channel channelBase, Locale locale) {
		if (parser.isEmpty()) return Mono.empty();
		
		if (!parser.couldBeChannelID()) {
			return expect(locale, parser, "command.error.channelid");
		}
		
		return parser.eatChannel(channelBase, locale)
			.map(channel -> channel.getID());
	}
	
	private Mono<Void> runCommand(Channel inputChannel, ChannelID outputChannelID, Locale locale) {
		return inputChannel.as(SuggestionChannel.type)
			.flatMap(c -> c.setOutput(outputChannelID));
	}
}
