package everyos.bot.luwu.run.command.modules.channel;

import everyos.bot.chat4j.entity.ChatPermission;
import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.CommandData;
import everyos.bot.luwu.core.entity.Channel;
import everyos.bot.luwu.core.entity.Locale;
import everyos.bot.luwu.core.functionality.channel.ChannelTextInterface;
import everyos.bot.luwu.run.command.CommandBase;
import reactor.core.publisher.Mono;

public class ResetChannelCommand extends CommandBase {

	public ResetChannelCommand() {
		super("command.resetchannel", e->true, ChatPermission.SEND_MESSAGES, ChatPermission.MANAGE_CHANNELS);
	}

	@Override
	public Mono<Void> execute(CommandData data, ArgumentParser parser) {
		Locale locale = data.getLocale();
		return runCommand(data.getChannel(), locale);
	}

	private Mono<Void> runCommand(Channel channel, Locale locale) {
		return channel.reset()
			.then(channel.getInterface(ChannelTextInterface.class).send(locale.localize("command.resetchannel.channelreset")))
			.then();
	}

}
