package everyos.bot.luwu.run.command.modules.info;

import everyos.bot.chat4j.entity.ChatPermission;
import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.CommandData;
import everyos.bot.luwu.core.entity.Channel;
import everyos.bot.luwu.core.entity.Locale;
import everyos.bot.luwu.core.functionality.channel.ChannelTextInterface;
import everyos.bot.luwu.run.command.CommandBase;
import reactor.core.publisher.Mono;

public class DonateCommand extends CommandBase {
	public DonateCommand() {
		super("command.donate", e->true, ChatPermission.SEND_MESSAGES, ChatPermission.NONE);
	}

	@Override
	public Mono<Void> execute(CommandData data, ArgumentParser parser) {
		return
			getDonateURL(data)
			.flatMap(url->sendDonateURL(data.getChannel(), url, data.getLocale()))
			.then();
	}

	private Mono<String> getDonateURL(CommandData data) {
		@SuppressWarnings("deprecation")
		String url = data.getBotEngine().getConfiguration().getCustomField("donate-url");
		return Mono.just(url==null?"(Misconfiguration)":url);
	}

	private Mono<Void> sendDonateURL(Channel channel, String url, Locale locale) {
		return
			channel.getInterface(ChannelTextInterface.class).send(locale.localize("command.donate.message", "url", url))
			.then();
	}
}
