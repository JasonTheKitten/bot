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

public class LinkSetRulesCommand extends CommandBase {
	
	public LinkSetRulesCommand() {
		super("command.link.setrules", e->true, ChatPermission.SEND_MESSAGES, ChatPermission.MANAGE_CHANNELS);
	}

	@Override
	public Mono<Void> execute(CommandData data, ArgumentParser parser) {
		Locale locale = data.getLocale();
		
		return LinkUtil.checkPerms(data.getChannel(), data.getInvoker(), locale)
			.then(parseArguments(parser, locale))
			.flatMap(rules->runCommand(data.getChannel(), rules, locale));
	}

	private Mono<String> parseArguments(ArgumentParser parser, Locale locale) {
		String rules = parser.getRemaining().strip();
		if (rules.length() > 800) {
			return Mono.error(new TextException("command.link.setrules.limit"));
		}
		return Mono.just(rules);
	}
	
	private Mono<Void> runCommand(Channel channel, String rules, Locale locale) {
		return channel.as(LinkChannel.type)
			.flatMap(clchannel->clchannel.getInfo())
			.flatMap(info -> info.getLink())
			.flatMap(link -> link.edit(spec->spec.setRules(rules))
				.then(link.sendSystemMessage(locale.localize("command.link.setrules.linkupdated")))
				.then(channel.getInterface(ChannelTextInterface.class)
					.send(locale.localize("command.link.setrules.message"))))
			.then();
	}
	
}
