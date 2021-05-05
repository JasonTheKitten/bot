package everyos.bot.luwu.run.command.modules.chatlink.moderation;

import everyos.bot.chat4j.entity.ChatPermission;
import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.CommandData;
import everyos.bot.luwu.core.entity.Locale;
import everyos.bot.luwu.core.entity.UserID;
import everyos.bot.luwu.run.command.CommandBase;
import everyos.bot.luwu.run.command.modules.chatlink.channel.ChatLinkChannel;
import everyos.bot.luwu.run.command.modules.chatlink.link.ChatLink;
import reactor.core.publisher.Mono;

public class LinkGlobalMuteCommand extends CommandBase {
	public LinkGlobalMuteCommand() {
		super("command.link.mute.global", e->true, ChatPermission.SEND_MESSAGES, ChatPermission.NONE);
	}

	@Override
	public Mono<Void> execute(CommandData data, ArgumentParser parser) {
		return
			parseArguments(parser, data.getLocale())
			.flatMap(uid->{
				return data.getChannel().as(ChatLinkChannel.type).flatMap(channel->channel.getLink())
					.flatMap(link->{
						//TODO: Ensure guild is member of link
						return muteUserGlobally(uid, link);
						//TODO: Link announcement
					});
			})
			.then();
	}
	
	private Mono<UserID> parseArguments(ArgumentParser parser, Locale locale) {
		return Mono.just(parser.eatUserID());
	}

	private Mono<Void> muteUserGlobally(UserID user, ChatLink link) {
		return link.addMutedUser(user);
	}
	
	//TODO: Perm check
}
