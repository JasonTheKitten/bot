package everyos.bot.luwu.run.command.modules.chatlink.setup;

import everyos.bot.chat4j.entity.ChatPermission;
import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.CommandData;
import everyos.bot.luwu.core.functionality.channel.ChannelTextInterface;
import everyos.bot.luwu.run.command.CommandBase;
import everyos.bot.luwu.run.command.modules.chatlink.channel.LinkChannel;
import everyos.bot.luwu.run.command.modules.chatlink.link.LinkUtil;
import reactor.core.publisher.Mono;

public class LinkCreateCommand extends CommandBase {
	
	public LinkCreateCommand() {
		super("command.link.create", e->true,
			ChatPermission.SEND_MESSAGES | ChatPermission.MANAGE_MESSAGES,
			ChatPermission.MANAGE_CHANNELS);
	}

	@Override
	public Mono<Void> execute(CommandData data, ArgumentParser parser) {
		return LinkUtil
			.create(data.getBotEngine())
			.flatMap(link->{
				ChannelTextInterface channel = data.getChannel().getInterface(ChannelTextInterface.class);
				return data
					.getChannel()
					.as(LinkChannel.type)
					.flatMap(linkChannel->linkChannel.edit(spec->{
						spec.setVerified(true);
						spec.setOpted(true);
						spec.setLinkID(link.getID());
					}))
					.then(channel.send(data.getLocale().localize("command.link.created", "id", String.valueOf(link.getID()))))
					.flatMap(message->message.pin());
			});
	}
	
}
