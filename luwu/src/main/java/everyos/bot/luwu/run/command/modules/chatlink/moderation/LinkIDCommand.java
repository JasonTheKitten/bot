package everyos.bot.luwu.run.command.modules.chatlink.moderation;

import everyos.bot.chat4j.entity.ChatPermission;
import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.CommandData;
import everyos.bot.luwu.core.entity.Channel;
import everyos.bot.luwu.core.entity.Locale;
import everyos.bot.luwu.core.functionality.channel.ChannelTextInterface;
import everyos.bot.luwu.run.command.CommandBase;
import everyos.bot.luwu.run.command.modules.chatlink.channel.LinkChannel;
import reactor.core.publisher.Mono;

public class LinkIDCommand extends CommandBase {
	
	public LinkIDCommand() {
		super("command.link.id", e->true, ChatPermission.SEND_MESSAGES, ChatPermission.NONE);
	}

	@Override
	public Mono<Void> execute(CommandData data, ArgumentParser parser) {
		Locale locale = data.getLocale();
		
		return parseArguments(parser, locale)
			.flatMap(notext -> runCommand(data.getChannel(), notext, locale));
	}

	private Mono<Boolean> parseArguments(ArgumentParser parser, Locale locale) {
		if (parser.isEmpty()) {
			return Mono.just(false);
		} else if (parser.getRemaining().equals("--notext") || parser.getRemaining().equals("-nt")) {
			return Mono.just(true);
		} else {
			return expect(locale, parser, "command.error.nothingorflag");
		}
	}

	private Mono<Void> runCommand(Channel channel, boolean notext, Locale locale) {
		return channel
			.as(LinkChannel.type)
			.flatMap(linkChannel -> linkChannel.getInfo())
			.flatMap(info -> {
				ChannelTextInterface textGrip = channel.getInterface(ChannelTextInterface.class);
				if (notext) {
					return textGrip.send(String.valueOf(info.getLinkID()));
				}
				return textGrip.send(locale.localize("command.link.linkid.message", "id", String.valueOf(info.getLinkID())));
			})
			.then();
	}

}
