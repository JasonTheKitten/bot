package everyos.bot.luwu.run.command.modules.chatlink.moderation;

import everyos.bot.chat4j.entity.ChatPermission;
import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.CommandData;
import everyos.bot.luwu.core.entity.Channel;
import everyos.bot.luwu.core.entity.Locale;
import everyos.bot.luwu.core.entity.User;
import everyos.bot.luwu.core.functionality.channel.ChannelTextInterface;
import everyos.bot.luwu.run.command.CommandBase;
import everyos.bot.luwu.run.command.modules.chatlink.channel.LinkChannel;
import everyos.bot.luwu.run.command.modules.chatlink.link.LinkUtil;
import everyos.bot.luwu.run.command.modules.chatlink.user.LinkUser;
import reactor.core.publisher.Mono;

public class LinkGlobalMuteCommand extends CommandBase {
	
	public LinkGlobalMuteCommand() {
		super("command.link.mute.global", e -> true, ChatPermission.SEND_MESSAGES, ChatPermission.NONE);
	}

	@Override
	public Mono<Void> execute(CommandData data, ArgumentParser parser) {
		Locale locale = data.getLocale();
		
		return LinkUtil.checkPerms(data.getChannel(), data.getInvoker(), locale)
			.then(parseArguments(parser, data.getLocale()))
			.flatMap(user -> runCommand(data.getChannel(), user, locale));
	}
	
	private Mono<User> parseArguments(ArgumentParser parser, Locale locale) {
		if (!parser.couldBeUserID()) {
			return expect(locale, parser, "command.error.userid");
		}
		
		return parser
			.eatUserID()
			.getUser();
	}
	
	private Mono<Void> runCommand(Channel channel, User user, Locale locale) {
		return channel
			.as(LinkChannel.type)
			.flatMap(clchannel -> clchannel.getInfo())
			.flatMap(info -> {
				return user.as(LinkUser.typeWith(info.getLinkID()))
					.flatMap(linkUser -> linkUser.edit(spec -> spec.setMuted(true)))
					.then(channel.getInterface(ChannelTextInterface.class).send(locale.localize("command.link.mute.global.success")))
					.then(info.getLink())
					.flatMap(link -> link.sendSystemMessage(locale.localize("command.link.mute.global.system", "user", user.getHumanReadableID())));
			})
			.then();
	}

}
