package everyos.bot.luwu.run.command.modules.chatlink.setup;

import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.CommandData;
import everyos.bot.luwu.core.functionality.channel.ChannelTextInterface;
import everyos.bot.luwu.run.command.CommandBase;
import everyos.bot.luwu.run.command.modules.chatlink.ChatLink;
import reactor.core.publisher.Mono;

public class LinkCreateCommand extends CommandBase {
	public LinkCreateCommand() {
		super("command.link.create");
	}

	@Override public Mono<Void> execute(CommandData data, ArgumentParser parser) {
		return ChatLink.createChatLink(data.getBotEngine())
			.flatMap(link->{
				ChannelTextInterface channel = data.getChannel().getInterface(ChannelTextInterface.class);
				return link.addChannel(data.getChannel())
					.flatMap(linkChannel->linkChannel.edit(spec->spec.setVerified(true)))
					.and(link.addAdmin(data.getChannel().getID()))
					.then(channel.send(data.getLocale().localize("command.link.created", "id", String.valueOf(link.getID()))))
					.flatMap(message->message.pin());
			});
	}
}
