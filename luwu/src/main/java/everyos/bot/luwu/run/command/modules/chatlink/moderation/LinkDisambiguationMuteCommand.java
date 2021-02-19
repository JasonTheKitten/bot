package everyos.bot.luwu.run.command.modules.chatlink.moderation;

import everyos.bot.chat4j.entity.ChatPermission;
import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.CommandData;
import everyos.bot.luwu.core.functionality.channel.ChannelTextInterface;
import everyos.bot.luwu.run.command.CommandBase;
import reactor.core.publisher.Mono;

public class LinkDisambiguationMuteCommand extends CommandBase {

	public LinkDisambiguationMuteCommand() {
		super("command.link.mute.disambiguation", e->true, ChatPermission.SEND_MESSAGES, ChatPermission.NONE);
	}

	@Override
	public Mono<Void> execute(CommandData data, ArgumentParser parser) {
		return data.getChannel().getInterface(ChannelTextInterface.class)
			.send(data.getLocale().localize("command.link.mute.disambiguation.message"))
			.then();
	}

}
