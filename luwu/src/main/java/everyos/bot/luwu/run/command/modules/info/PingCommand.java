package everyos.bot.luwu.run.command.modules.info;

import everyos.bot.chat4j.entity.ChatPermission;
import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.CommandData;
import everyos.bot.luwu.core.command.Shard;
import everyos.bot.luwu.core.entity.Channel;
import everyos.bot.luwu.core.entity.Locale;
import everyos.bot.luwu.core.functionality.channel.ChannelTextInterface;
import everyos.bot.luwu.run.command.CommandBase;
import reactor.core.publisher.Mono;

public class PingCommand extends CommandBase {
	public PingCommand() {
		super("command.ping", e->true, ChatPermission.SEND_MESSAGES, ChatPermission.NONE);
	}

	@Override
	public Mono<Void> execute(CommandData data, ArgumentParser parser) {
		Locale locale = data.getLocale();
		
		return runCommand(data.getChannel(), data.getShard(), locale);
	}

	private Mono<Void> runCommand(Channel channel, Shard shard, Locale locale) {
		return channel.getInterface(ChannelTextInterface.class).send(locale.localize("command.ping.message",
			"ping", String.valueOf(shard.getPing())))
			.then();
	}
}
