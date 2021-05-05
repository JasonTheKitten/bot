package everyos.bot.luwu.run.command.modules.welcome;

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

public class WelcomeCommand extends CommandBase {
	private boolean isWelcome;

	public WelcomeCommand(boolean isWelcome) {
		super(isWelcome?"command.welcome":"command.leave", e->true,
			ChatPermission.SEND_MESSAGES, ChatPermission.MANAGE_MEMBERS);
		
		this.isWelcome = isWelcome;
	}

	@Override
	public Mono<Void> execute(CommandData data, ArgumentParser parser) {
		Locale locale = data.getLocale();
		
		return
			parseArgs(parser, locale)
			.flatMap(message->runCommand(data.getChannel(), message.getT1(), message.getT2(), locale));
	}

	private Mono<Tuple<ChannelID, String>> parseArgs(ArgumentParser parser, Locale locale) {
		if (parser.isEmpty()) {
			return Mono.just(Tuple.of(null, null));
		}
		
		if (!parser.couldBeChannelID()) {
			return expect(locale, parser, "command.error.channelid");
		}
		ChannelID channelID = parser.eatUncheckedChannelID();
		
		if (parser.isEmpty()) {
			return expect(locale, parser, "command.error.string");
		}
		//TODO: Text length limit
		
		return Mono.just(Tuple.of(channelID, parser.getRemaining()));
	}
	
	private Mono<Void> runCommand(Channel channel, ChannelID output, String message, Locale locale) {
		return
			channel.getServer()
			.flatMap(server->server.as(WelcomeServer.type))
			.flatMap(server->server.edit(spec->{
				if (isWelcome) {
					spec.setWelcomeMessage(output, message);
				} else {
					spec.setLeaveMessage(output, message);
				}
			}))
			.then(channel.getInterface(ChannelTextInterface.class).send(locale.localize("command.welcome.messageset")))
			.then();
	}
}
