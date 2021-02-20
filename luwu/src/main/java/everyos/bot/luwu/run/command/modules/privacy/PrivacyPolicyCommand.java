package everyos.bot.luwu.run.command.modules.privacy;

import everyos.bot.chat4j.entity.ChatPermission;
import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.CommandData;
import everyos.bot.luwu.core.entity.Channel;
import everyos.bot.luwu.core.entity.Locale;
import everyos.bot.luwu.core.functionality.channel.ChannelTextInterface;
import everyos.bot.luwu.run.command.CommandBase;
import reactor.core.publisher.Mono;

public class PrivacyPolicyCommand extends CommandBase {
	public PrivacyPolicyCommand() {
		super("command.privacypolicy", e->true, ChatPermission.SEND_MESSAGES, ChatPermission.NONE);
	}

	@Override
	public Mono<Void> execute(CommandData data, ArgumentParser parser) {
		return
			getFeatureURL(data)
			.flatMap(url->sendFeatureURL(data.getChannel(), url, data.getLocale()))
			.then();
	}

	private Mono<String> getFeatureURL(CommandData data) {
		@SuppressWarnings("deprecation")
		String url = data.getBotEngine().getConfiguration().getCustomField("privacypolicy-url");
		return Mono.just(url==null?"(Misconfiguration)":url);
	}

	private Mono<Void> sendFeatureURL(Channel channel, String url, Locale locale) {
		return
			channel.getInterface(ChannelTextInterface.class).send(locale.localize("command.privacypolicy.message", "url", url))
			.then();
	}
}
