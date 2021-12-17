package everyos.bot.luwu.run.command.modules.chatlink.moderation;

import everyos.bot.chat4j.entity.ChatPermission;
import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.CommandData;
import everyos.bot.luwu.core.entity.Channel;
import everyos.bot.luwu.core.entity.Locale;
import everyos.bot.luwu.core.entity.Member;
import everyos.bot.luwu.core.functionality.channel.ChannelTextInterface;
import everyos.bot.luwu.run.command.CommandBase;
import everyos.bot.luwu.run.command.modules.chatlink.channel.LinkChannel;
import everyos.bot.luwu.run.command.modules.chatlink.user.LinkUser;
import reactor.core.publisher.Mono;

public class LinkOptOutCommand extends CommandBase {
	
	public LinkOptOutCommand() {
		super("command.link.optout", e -> true, ChatPermission.NONE, ChatPermission.NONE);
	}

	@Override
	public Mono<Void> execute(CommandData data, ArgumentParser parser) {
		return runCommand(data.getChannel(), data.getInvoker(), data.getLocale());
	}

	private Mono<Void> runCommand(Channel channel, Member invoker, Locale locale) {
		return getLinkIDFromChannel(channel)
			.flatMap(linkID -> invoker.as(LinkUser.typeWith(linkID)))
			.flatMap(user -> user.edit(spec -> spec.setVerified(false)))
			.then(channel.getInterface(ChannelTextInterface.class)
				.send(locale.localize("command.link.optout.message")))
			.then();
	}

	private Mono<Long> getLinkIDFromChannel(Channel channel) {
		return channel
			.as(LinkChannel.type)
			.flatMap(clchannel -> clchannel.getInfo())
			.map(info->info.getLinkID());
	}
	
}
