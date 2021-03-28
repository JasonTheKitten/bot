package everyos.bot.luwu.run.command.modules.chatlink.moderation;

import everyos.bot.chat4j.entity.ChatPermission;
import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.CommandData;
import everyos.bot.luwu.core.entity.Channel;
import everyos.bot.luwu.core.entity.Locale;
import everyos.bot.luwu.run.command.CommandBase;
import everyos.bot.luwu.run.command.modules.chatlink.ChatLinkChannel;
import reactor.core.publisher.Mono;

public class LinkSetRulesCommand extends CommandBase {
	public LinkSetRulesCommand() {
		super("command.link.rules", e->true, ChatPermission.SEND_MESSAGES, ChatPermission.NONE);
	}

	@Override
	public Mono<Void> execute(CommandData data, ArgumentParser parser) {
		Locale locale = data.getLocale();
		
		return parseArguments(parser, locale)
			.flatMap(rules->runCommand(data.getChannel(), rules, locale));
	}

	private Mono<String> parseArguments(ArgumentParser parser, Locale locale) {
		return null;
	}
	
	private Mono<Void> runCommand(Channel channel, String rules, Locale locale) {
		return channel.as(ChatLinkChannel.type)
			.flatMap(c->c.getLink())
			.flatMap(link->{
				return link.setRules(rules);
			});
	}
}
