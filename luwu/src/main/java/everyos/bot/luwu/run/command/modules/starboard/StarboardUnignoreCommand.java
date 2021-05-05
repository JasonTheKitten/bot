package everyos.bot.luwu.run.command.modules.starboard;

import everyos.bot.chat4j.entity.ChatPermission;
import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.CommandData;
import everyos.bot.luwu.core.entity.Channel;
import everyos.bot.luwu.core.entity.ChannelID;
import everyos.bot.luwu.core.entity.Locale;
import everyos.bot.luwu.core.functionality.channel.ChannelTextInterface;
import everyos.bot.luwu.run.command.CommandBase;
import reactor.core.publisher.Mono;

public class StarboardUnignoreCommand extends CommandBase {
	public StarboardUnignoreCommand() {
		super("command.starboard.unignore", e->true,
			ChatPermission.SEND_MESSAGES,
			ChatPermission.MANAGE_CHANNELS);
	}

	@Override
	public Mono<Void> execute(CommandData data, ArgumentParser parser) {
		Locale locale = data.getLocale();
		
		return
			parseArgs(parser, locale)
			.flatMap(channelID->runCommand(data.getChannel(), channelID, locale))
			.then();
	}

	private Mono<ChannelID> parseArgs(ArgumentParser parser, Locale locale) {
		if (!parser.couldBeChannelID()) {
			return expect(locale, parser, "command.error.channelid");
		}
		
		ChannelID channelID = parser.eatUncheckedChannelID();

		return Mono.just(channelID);
	}
	
	private Mono<Void> runCommand(Channel channel, ChannelID channelID, Locale locale) {
		return channel.getServer()
			.flatMap(s->s.as(StarboardServer.type))
			.flatMap(c->c.edit(spec->{
				spec.unignoreChannel(channelID);
			}))
			.then(channel.getInterface(ChannelTextInterface.class).send("command.starboard.unignore.message"))
			.then();
	}
}