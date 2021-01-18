package everyos.bot.luwu.run.command.modules.chatlink.moderation;

import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.CommandData;
import everyos.bot.luwu.core.entity.Locale;
import everyos.bot.luwu.core.entity.UserID;
import everyos.bot.luwu.run.command.CommandBase;
import everyos.bot.luwu.run.command.modules.chatlink.ChatLinkChannel;
import reactor.core.publisher.Mono;

public class LinkLocalMuteCommand extends CommandBase {
	public LinkLocalMuteCommand() {
		super("command.link.mute.local");
	}

	@Override
	public Mono<Void> execute(CommandData data, ArgumentParser parser) {
		return
			parseArguments(parser, data.getLocale())
			.flatMap(uid->{
				return data.getChannel().as(ChatLinkChannel.type)
					.flatMap(channel->{
						//TODO: Ensure guild is member of link
						return muteUserLocally(uid, channel);
					});
			})
			.then();
	}
	
	private Mono<UserID> parseArguments(ArgumentParser parser, Locale locale) {
		return Mono.just(parser.eatUserID());
	}

	private Mono<Void> muteUserLocally(UserID user, ChatLinkChannel channel) {
		return channel.edit(spec->{
			spec.addMutedUser(user);
		});
	}
	
	//TODO: Perm check
}
