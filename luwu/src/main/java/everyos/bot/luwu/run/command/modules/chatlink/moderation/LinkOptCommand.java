package everyos.bot.luwu.run.command.modules.chatlink.moderation;

import everyos.bot.chat4j.entity.ChatPermission;
import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.CommandData;
import everyos.bot.luwu.core.entity.Channel;
import everyos.bot.luwu.core.entity.Locale;
import everyos.bot.luwu.core.exception.TextException;
import everyos.bot.luwu.core.functionality.channel.ChannelTextInterface;
import everyos.bot.luwu.run.command.CommandBase;
import everyos.bot.luwu.run.command.modules.chatlink.channel.LinkChannel;
import everyos.bot.luwu.run.command.modules.chatlink.link.LinkUtil;
import reactor.core.publisher.Mono;

public class LinkOptCommand extends CommandBase {
	
	public LinkOptCommand() {
		super("command.link.opt", e->true, ChatPermission.SEND_MESSAGES, ChatPermission.NONE);
	}

	@Override
	public Mono<Void> execute(CommandData data, ArgumentParser parser) {
		Locale locale = data.getLocale();
		
		return LinkUtil.checkPerms(data.getChannel(), data.getInvoker(), locale)
			.then(parseArguments(parser, data.getLocale()))
			.flatMap(cid -> runCommand(data.getChannel(), cid, locale));
	}
	
	private Mono<LinkChannel> parseArguments(ArgumentParser parser, Locale locale) {
		if (!parser.couldBeChannelID()) {
			return expect(locale, parser, "command.error.channelid");
		}
		
		return parser
			.eatUncheckedChannelID()
			.getChannel()
			.flatMap(channel -> channel.as(LinkChannel.type)); 
	}
	
	private Mono<Void> runCommand(Channel channel, LinkChannel siblingChannel, Locale locale) {
		return channel
			.as(LinkChannel.type)
			.flatMap(clchannel -> ensureInSameLinks(clchannel, siblingChannel, locale))
			.then(siblingChannel.edit(spec -> spec.setOpted(true)))
			.then(channel.getInterface(ChannelTextInterface.class).send(locale.localize("command.link.opt.message")))
			.then();
	}
	
	private Mono<Void> ensureInSameLinks(LinkChannel channelA, LinkChannel channelB, Locale locale) {
		return channelA
			.getInfo()
			.flatMap(infoA -> {
				return channelB
					.getInfo()
					.map(infoB -> {
						return infoA.getLinkID() == infoB.getLinkID();
					});
			})
			.filter(v -> v)
			.switchIfEmpty(Mono.error(new TextException(locale.localize("command.link.opt.difflinks"))))
			.then();
	}
	
}
