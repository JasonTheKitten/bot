package everyos.bot.luwu.run.command.modules.easteregg;

import everyos.bot.chat4j.entity.ChatPermission;
import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.CommandData;
import everyos.bot.luwu.core.entity.Locale;
import everyos.bot.luwu.core.functionality.channel.ChannelTextInterface;
import everyos.bot.luwu.run.command.CommandBase;
import reactor.core.publisher.Mono;

public class UwUCommand extends CommandBase {
	public UwUCommand() {
		super("command.easteregg.uwu", e->true, ChatPermission.SEND_MESSAGES, ChatPermission.NONE);
	}

	@Override
	public Mono<Void> execute(CommandData data, ArgumentParser parser) {
		Locale locale = data.getLocale();
		
		String text = locale.localize("bot.name.playful");
		if (parser.getRemaining().equals("green")) { // Doesn't get localized
			text = "Green is not a creative color.";
		}
		
		return data.getChannel().getInterface(ChannelTextInterface.class)
			.send(
				String.format("> %s (User ID: %s)\n %s",
					parser.getOriginal(),
					data.getInvoker().getID().getLong(),
					text))
			.then();
	}
}
