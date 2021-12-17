package everyos.bot.luwu.run.command.modules.chatlink.moderation;

import java.util.Optional;

import everyos.bot.chat4j.entity.ChatPermission;
import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.CommandData;
import everyos.bot.luwu.core.entity.Channel;
import everyos.bot.luwu.core.entity.Locale;
import everyos.bot.luwu.core.functionality.channel.ChannelTextInterface;
import everyos.bot.luwu.run.command.CommandBase;
import everyos.bot.luwu.run.command.modules.chatlink.channel.LinkChannel;
import reactor.core.publisher.Mono;

public class LinkRulesCommand extends CommandBase {
	public LinkRulesCommand() {
		super("command.link.rules", e->true, ChatPermission.SEND_MESSAGES, ChatPermission.NONE);
	}

	@Override
	public Mono<Void> execute(CommandData data, ArgumentParser parser) {
		Locale locale = data.getLocale();
		
		return
			runCommand(data.getChannel(), locale);
	}

	private Mono<Void> runCommand(Channel channel, Locale locale) {
		return channel.as(LinkChannel.type)
			.flatMap(clchannel -> clchannel.getInfo())
			.flatMap(info -> info.getLink())
			.map(link -> link.getInfo())
			.flatMap(info -> {
				ChannelTextInterface textGrip = channel.getInterface(ChannelTextInterface.class);
				Optional<String> rules = info.getRules();
				if (rules.isPresent()) {
					return textGrip.send(locale.localize("command.link.rules.message", "rules", rules.get()));
				} else {
					return textGrip.send(locale.localize("command.link.rules.unset"));
				}
			}).then();
	}
}
