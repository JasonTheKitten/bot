package everyos.bot.luwu.run.command.modules.chatlink.setup;

import everyos.bot.luwu.core.BotEngine;
import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.Command;
import everyos.bot.luwu.core.command.CommandData;
import everyos.bot.luwu.core.entity.Channel;
import everyos.bot.luwu.core.entity.Locale;
import everyos.bot.luwu.core.entity.Member;
import everyos.bot.luwu.core.exception.TextException;
import everyos.bot.luwu.core.functionality.channel.ChannelTextInterface;
import everyos.bot.luwu.run.command.modules.chatlink.ChatLink;
import everyos.bot.luwu.run.command.modules.chatlink.ChatLinkChannel;
import reactor.core.publisher.Mono;

public class LinkJoinCommand implements Command {
	@Override public Mono<Void> execute(CommandData data, ArgumentParser parser) {
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
			return Mono.error(new TextException(locale.localize("command.error.usage", "expected", locale.localize("user"), "got", "nothing")));
		}
		return Mono.just(parser.getRemaining().trim());
	}
	private Mono<Void> joinLink(BotEngine bot, ChatLink link, Channel channel, Locale locale) {
		//To join a link
		//  Lookup the link
		//  Convert our channel to a link channel
		//  If the link is free-join, auto-verify, else, prompt verification
		//  If the link is DMing join requests, send the DM
		ChannelTextInterface textGrip = channel.getInterface(ChannelTextInterface.class);
		
		Mono<Void> nextAction =
			textGrip.send(locale.localize("command.link.pleaseverify", "id", String.valueOf(link.getID())))
			.then();
		if (link.isAutoVerify()) {
			nextAction = channel
				.as(ChatLinkChannel.type)
				.edit(spec->spec.setVerified(true))
				.then(textGrip.send(locale.localize("command.link.autoverify", "id", String.valueOf(link.getID()))))
				.then();
		}
		
		return
			link.addChannel(channel)
			.then(nextAction);
	}
	private Mono<ChatLink> lookupLink(BotEngine bot, String id, Locale locale) {
		try {
			return ChatLink.getByID(bot, Long.valueOf(id));
		} catch (NumberFormatException e) {
			return ChatLink.getByName(bot, id);
		}
	}
}
