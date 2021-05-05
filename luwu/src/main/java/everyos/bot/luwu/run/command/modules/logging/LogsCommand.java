package everyos.bot.luwu.run.command.modules.logging;

import everyos.bot.chat4j.entity.ChatPermission;
import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.CommandData;
import everyos.bot.luwu.core.entity.Channel;
import everyos.bot.luwu.core.entity.ChannelID;
import everyos.bot.luwu.core.entity.Locale;
import everyos.bot.luwu.core.functionality.channel.ChannelTextInterface;
import everyos.bot.luwu.run.command.CommandBase;
import reactor.core.publisher.Mono;

public class LogsCommand extends CommandBase {
	public LogsCommand() {
		super("command.logs", e->true,
			ChatPermission.SEND_MESSAGES | ChatPermission.SEND_EMBEDS,
			ChatPermission.MANAGE_MESSAGES);
	}

	@Override
	public Mono<Void> execute(CommandData data, ArgumentParser parser) {
		Locale locale = data.getLocale();
		
		return
			parseArguments(parser, locale)
			.flatMap(channelID->runCommand(data.getChannel(), channelID, locale).then(Mono.just(true)))
			.switchIfEmpty(disableLogs(data.getChannel(), locale))
			.then();
	}

	private Mono<ChannelID> parseArguments(ArgumentParser parser, Locale locale) {
		if (parser.isEmpty()) {
			return Mono.empty();
		}
		
		if (!parser.couldBeChannelID()) {
			return expect(locale, parser, "");
		}
		
		return Mono.just(parser.eatUncheckedChannelID());
	}
	
	private Mono<Void> runCommand(Channel channel, ChannelID channelID, Locale locale) {
		return channel.getServer()
			.flatMap(server->server.as(LogsServer.type))
			.flatMap(server->server.edit(spec->{
				spec.setLogChannel(channelID);
				return Mono.empty();
			}))
			.then(channel.getInterface(ChannelTextInterface.class).send(locale.localize("command.logs.setmessage")))
			.then();
	}

	private Mono<Boolean> disableLogs(Channel channel, Locale locale) {
		return channel.getServer()
			.flatMap(server->server.as(LogsServer.type))
			.flatMap(server->server.edit(spec->{
				spec.clear();
				return Mono.empty();
			}))
			.then(channel.getInterface(ChannelTextInterface.class).send(locale.localize("command.logs.unsetmessage")))
			.then(Mono.just(true));
	}
}
