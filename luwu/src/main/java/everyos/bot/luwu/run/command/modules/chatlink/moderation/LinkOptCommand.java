package everyos.bot.luwu.run.command.modules.chatlink.moderation;

import everyos.bot.chat4j.entity.ChatPermission;
import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.CommandData;
import everyos.bot.luwu.core.entity.ChannelID;
import everyos.bot.luwu.core.entity.Locale;
import everyos.bot.luwu.run.command.CommandBase;
import everyos.bot.luwu.run.command.modules.chatlink.ChatLink;
import everyos.bot.luwu.run.command.modules.chatlink.ChatLinkChannel;
import reactor.core.publisher.Mono;

public class LinkOptCommand extends CommandBase {
	public LinkOptCommand() {
		super("command.link.opt", e->true, ChatPermission.SEND_MESSAGES, ChatPermission.NONE);
	}

	@Override
	public Mono<Void> execute(CommandData data, ArgumentParser parser) {
		return
			parseArguments(parser, data.getLocale())
			.flatMap(cid->{
				return data.getChannel().as(ChatLinkChannel.type).flatMap(channel->channel.getLink())
					.flatMap(link->{
						//TODO: Ensure guild is member of link
						return optChannel(cid, link);
					});
			})
			.then();
	}
	
	private Mono<ChannelID> parseArguments(ArgumentParser parser, Locale locale) {
		return Mono.just(parser.eatChannelID()); //TODO: 
	}

	private Mono<Void> optChannel(ChannelID channel, ChatLink link) {
		return link.addAdmin(channel);
	}
	
	//TODO: Perm check
}
