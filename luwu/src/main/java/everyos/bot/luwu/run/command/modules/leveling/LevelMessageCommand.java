package everyos.bot.luwu.run.command.modules.leveling;

import everyos.bot.chat4j.entity.ChatPermission;
import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.CommandData;
import everyos.bot.luwu.core.entity.Channel;
import everyos.bot.luwu.core.entity.ChannelID;
import everyos.bot.luwu.core.entity.Locale;
import everyos.bot.luwu.core.entity.Member;
import everyos.bot.luwu.core.exception.TextException;
import everyos.bot.luwu.core.functionality.channel.ChannelTextInterface;
import everyos.bot.luwu.run.command.CommandBase;
import everyos.bot.luwu.util.Tuple;
import reactor.core.publisher.Mono;

public class LevelMessageCommand extends CommandBase {
	public LevelMessageCommand() {
		super("command.level.message", e->true, ChatPermission.SEND_MESSAGES, ChatPermission.MANAGE_GUILD);
	}

	@Override
	public Mono<Void> execute(CommandData data, ArgumentParser parser) {
		Locale locale = data.getLocale();
		
		return
			checkPerms(data.getInvoker()) //TODO: Moving this to CommandBase
			.then(parseArgs(locale, parser))
			.flatMap(tup->setMessage(locale, data.getChannel(), tup.getT1(), tup.getT2()));
			
	}

	private Mono<Void> checkPerms(Member member) {
		return Mono.empty();
	}
	
	private Mono<Tuple<ChannelID, String>> parseArgs(Locale locale, ArgumentParser parser) {
		if (!parser.couldBeChannelID()) return expect(locale, parser, "command.error.channelid");
		ChannelID channelID = parser.eatChannelID();
		//TODO: Validate channel ID
		
		if (parser.isEmpty()) return expect(locale, parser, "command.error.string");
		String message = parser.getRemaining();
		
		return Mono.just(Tuple.of(channelID, message));
	}

	private Mono<Void> setMessage(Locale locale, Channel channel, ChannelID channelID, String message) {
		return 
			channel.getServer().map(server->server.getWithExtension(LevelServer.type)).flatMap(server->{
				return server.getLevelInfo().flatMap(info->{
					if (!info.getLevellingEnabled()) {
						return Mono.error(new TextException(locale.localize("command.level.error.disabled")));
					}
					
					return server.setLevelInfo(new LevelInfo(true, channelID, message));
				});
			})
			.then(channel.getInterface(ChannelTextInterface.class).send(
				locale.localize("command.level.messageset",
					"message", message,
					"channel", channelID.toString())))
			.then();
	}
}
