package everyos.bot.luwu.run.command.modules.easteregg;

import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.CommandData;
import everyos.bot.luwu.core.entity.Locale;
import everyos.bot.luwu.core.functionality.channel.ChannelTextInterface;
import everyos.bot.luwu.run.command.CommandBase;
import reactor.core.publisher.Mono;

public class UwUCommand extends CommandBase {
	public UwUCommand() {
		super("command.easteregg.uwu");
	}

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
