package everyos.bot.luwu.run.command.modules.chatlink.moderation;

import everyos.bot.chat4j.entity.ChatPermission;
import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.CommandData;
import everyos.bot.luwu.core.entity.Channel;
import everyos.bot.luwu.core.entity.Locale;
import everyos.bot.luwu.core.entity.UserID;
import everyos.bot.luwu.core.functionality.channel.ChannelTextInterface;
import everyos.bot.luwu.run.command.CommandBase;
import everyos.bot.luwu.run.command.modules.chatlink.channel.LinkChannel;
import reactor.core.publisher.Mono;

public class LinkLocalMuteCommand extends CommandBase {
	
	public LinkLocalMuteCommand() {
		super("command.link.mute.local", e -> true, ChatPermission.SEND_MESSAGES, ChatPermission.MANAGE_MEMBERS);
	}

	@Override
	public Mono<Void> execute(CommandData data, ArgumentParser parser) {
		Locale locale = data.getLocale();
		
		return
			parseArguments(parser, locale)
			.flatMap(uid -> runCommand(uid, data.getChannel(), locale))
			.then();
	}
	
	private Mono<UserID> parseArguments(ArgumentParser parser, Locale locale) {
		if (!parser.couldBeUserID()) {
			return expect(locale, parser, "command.error.userid");
		}
		
		return Mono.just(parser.eatUserID());
	}
	
	private Mono<Void> runCommand(UserID user, Channel channel, Locale locale) {
		return channel.as(LinkChannel.type)
			.flatMap(clchannel -> muteUserLocally(user, clchannel, locale));
			//TODO: Ensure guild is member of link
	}

	private Mono<Void> muteUserLocally(UserID user, LinkChannel channel, Locale locale) {
		return
			channel.edit(spec -> spec.addMutedUser(user))
			.then(channel.getInterface(ChannelTextInterface.class).send(locale.localize("command.link.mute.local.success")))
			.then();
	}
	
}
