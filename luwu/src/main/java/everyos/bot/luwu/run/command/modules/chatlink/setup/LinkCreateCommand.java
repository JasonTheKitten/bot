package everyos.bot.luwu.run.command.modules.chatlink.setup;

import everyos.bot.chat4j.functionality.channel.ChatChannelTextInterface;
import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.Command;
import everyos.bot.luwu.core.command.CommandData;
import everyos.bot.luwu.run.command.modules.chatlink.ChatLink;
import reactor.core.publisher.Mono;

public class LinkCreateCommand implements Command {
	@Override public Mono<Void> execute(CommandData data, ArgumentParser parser) {
		return ChatLink.createChatLink(data.getBotEngine())
			.flatMap(link->{
				ChatChannelTextInterface channel = data.getChannel().getInterface(ChatChannelTextInterface.class);
				return link.addChannel(data.getChannel())
					.flatMap(linkChannel->linkChannel.edit(spec->spec.setVerified(true)))
					.then(channel.send(data.getLocale().localize("command.chatlink.created", "id", String.valueOf(link.getID()))))
					.flatMap(message->message.pin());
			});
	}
}
