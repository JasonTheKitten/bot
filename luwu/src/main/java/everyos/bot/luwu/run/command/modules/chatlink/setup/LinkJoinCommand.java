package everyos.bot.luwu.run.command.modules.chatlink.setup;

import everyos.bot.chat4j.entity.ChatPermission;
import everyos.bot.luwu.core.BotEngine;
import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.CommandData;
import everyos.bot.luwu.core.entity.Channel;
import everyos.bot.luwu.core.entity.Locale;
import everyos.bot.luwu.core.entity.Member;
import everyos.bot.luwu.core.exception.TextException;
import everyos.bot.luwu.core.functionality.channel.ChannelTextInterface;
import everyos.bot.luwu.run.command.CommandBase;
import everyos.bot.luwu.run.command.modules.chatlink.channel.LinkChannel;
import everyos.bot.luwu.run.command.modules.chatlink.link.Link;
import everyos.bot.luwu.run.command.modules.chatlink.link.LinkInfo;
import everyos.bot.luwu.run.command.modules.chatlink.link.LinkUtil;
import reactor.core.publisher.Mono;

public class LinkJoinCommand extends CommandBase {
	public LinkJoinCommand() {
		super("command.link.join", e->true, ChatPermission.SEND_MESSAGES, ChatPermission.MANAGE_MESSAGES);
	}

	@Override
	public Mono<Void> execute(CommandData data, ArgumentParser parser) {
		//Get the channel
		//Check that we have proper perms, throwing if not
		//Parse arguments for link id, throwing if not provided
		//Get the link to join, throw if fails
		//Join the link
		
		Locale locale = data.getLocale();
		
		return
			checkPerms(data.getInvoker(), locale)
			.then(parseArgs(parser, locale))
			.flatMap(id->lookupLink(data.getBotEngine(), id, locale))
			.flatMap(link->joinLink(data.getBotEngine(), link, data.getChannel(), locale));
	}
	
	private Mono<Void> checkPerms(Member member, Locale locale) {
		return Mono.empty(); //TODO: Throw if not valid perms
	}
	
	private Mono<String> parseArgs(ArgumentParser parser, Locale locale) {
		if (parser.isEmpty()) {
			return Mono.error(new TextException(locale.localize("command.error.usage", "expected", locale.localize("command.link.linkid"), "got", "nothing")));
		}
		return Mono.just(parser.getRemaining().trim());
	}
	
	private Mono<Void> joinLink(BotEngine bot, Link link, Channel channel, Locale locale) {
		//To join a link
		//  Lookup the link
		//  Convert our channel to a link channel
		//  If the link is free-join, auto-verify, else, prompt verification
		//  If the link is DMing join requests, send the DM
		ChannelTextInterface textGrip = channel.getInterface(ChannelTextInterface.class);
		LinkInfo info = link.getInfo();
		
		return channel
			.as(LinkChannel.type)
			.flatMap(linkChannel -> linkChannel.edit(spec -> {
				spec.setLinkID(link.getID());
				spec.setVerified(info.isAutoVerify());
			}))
			.then(
				info.isAutoVerify() ?
					textGrip.send(locale.localize("command.link.autoverify", "id", String.valueOf(channel.getID()))) :
					textGrip.send(locale.localize("command.link.pleaseverify", "id", String.valueOf(channel.getID())))
			).then();
	}
	
	private Mono<Link> lookupLink(BotEngine bot, String id, Locale locale) {
		try {
			return LinkUtil.getByID(bot, Long.valueOf(id));
		} catch (NumberFormatException e) {
			return LinkUtil.getByName(bot, id);
		}
	}
}
