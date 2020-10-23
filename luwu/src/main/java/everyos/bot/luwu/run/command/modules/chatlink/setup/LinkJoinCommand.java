package everyos.bot.luwu.run.command.modules.chatlink.setup;

import everyos.bot.luwu.core.BotEngine;
import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.Command;
import everyos.bot.luwu.core.command.CommandData;
import everyos.bot.luwu.core.entity.Channel;
import everyos.bot.luwu.core.entity.Locale;
import everyos.bot.luwu.core.entity.Member;
import everyos.bot.luwu.core.exception.TextException;
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
			.flatMap(id->joinLink(data.getBotEngine(), id, data.getChannel(), locale));
	}
	
	private Mono<Void> checkPerms(Member member, Locale locale) {
		return Mono.empty(); //TODO: Throw if not valid perms
	}
	private Mono<String> parseArgs(ArgumentParser parser, Locale locale) {
		if (parser.isEmpty()) {
			return Mono.error(new TextException(locale.localize("command.error.usage", "expected", locale.localize("user"), "got", "nothing")));
		}
		return Mono.just(parser.toString().trim());
	}
	private Mono<Void> joinLink(BotEngine bot, String id, Channel channel, Locale locale) {
		//To join a link
		//  Lookup the link
		//  Convert our channel to a link channel
		//  If the link is free-join, auto-verify, else, prompt verification
		//  If the link is DMing join requests, send the DM
		return
			lookupLink(bot, id, locale)
			.flatMap(link->{
				Mono<Void> nextAction = Mono.empty();
				if (link.isAutoVerify()) {
					nextAction =
						channel.as(ChatLinkChannel.type)
							.edit(spec->spec.setVerified(true));
				}
				
				return
					link.addChannel(channel)
					.then(nextAction);
			});
	}
	private Mono<ChatLink> lookupLink(BotEngine bot, String id, Locale locale) {
		return ChatLink.getByName(bot, id);
	}
}
