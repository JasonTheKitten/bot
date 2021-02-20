package everyos.bot.luwu.run.command.modules.oneword.moderation;

import everyos.bot.chat4j.entity.ChatPermission;
import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.CommandData;
import everyos.bot.luwu.core.entity.Channel;
import everyos.bot.luwu.core.entity.Locale;
import everyos.bot.luwu.core.functionality.channel.ChannelTextInterface;
import everyos.bot.luwu.run.command.CommandBase;
import everyos.bot.luwu.run.command.modules.oneword.OneWordChannel;
import reactor.core.publisher.Mono;

public class OneWordResetCommand extends CommandBase {

	public OneWordResetCommand() {
		super("command.oneword.moderation.reset", e->true,
			ChatPermission.SEND_MESSAGES,
			ChatPermission.MANAGE_MESSAGES);
	}

	@Override
	public Mono<Void> execute(CommandData data, ArgumentParser parser) {
		Locale locale = data.getLocale();
		
		return runCommand(data.getChannel(), locale);
	}

	private Mono<Void> runCommand(Channel channel, Locale locale) {
		return channel.as(OneWordChannel.type)
			.flatMap(c->c.reset())
			.then(channel.getInterface(ChannelTextInterface.class).send(locale.localize("command.oneword.moderation.reset.message")))
			.then();
	}

}
