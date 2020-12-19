package everyos.bot.luwu.run.command.modules.fun;

import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.Command;
import everyos.bot.luwu.core.command.CommandData;
import everyos.bot.luwu.core.entity.Locale;
import everyos.bot.luwu.core.functionality.channel.ChannelTextInterface;
import reactor.core.publisher.Mono;

public class UwUCommand implements Command {
	@Override
	public Mono<Void> execute(CommandData data, ArgumentParser parser) {
		Locale locale = data.getLocale();
		
		return data.getChannel().getInterface(ChannelTextInterface.class)
			.send(
				String.format("> %s (User ID: %s)\n %s",
					parser.getOriginal(),
					data.getInvoker().getID().getLong(),
					locale.localize("bot.name.playful")))
			.then();
	}
}
