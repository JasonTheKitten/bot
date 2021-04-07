package everyos.bot.luwu.run.command.modules.utility;

import everyos.bot.chat4j.entity.ChatPermission;
import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.CommandData;
import everyos.bot.luwu.core.entity.Channel;
import everyos.bot.luwu.core.entity.Locale;
import everyos.bot.luwu.core.entity.UserID;
import everyos.bot.luwu.core.functionality.channel.ChannelTextInterface;
import everyos.bot.luwu.run.command.CommandBase;
import everyos.bot.luwu.util.TranslateUtil;
import everyos.bot.luwu.util.Tuple;
import reactor.core.publisher.Mono;

public class TranslateCommand extends CommandBase {
	public TranslateCommand() {
		super("command.translate", e->true, ChatPermission.SEND_MESSAGES, ChatPermission.NONE);
	}

	@Override
	public Mono<Void> execute(CommandData data, ArgumentParser parser) {
		Locale locale = data.getLocale();
		
		return
			parseArgs(parser, locale)
			.flatMap(tup->runCommand(data.getChannel(), tup.getT1().getT1(), tup.getT1().getT2(), tup.getT2(), data.getInvoker().getID(), locale));
	}

	private Mono<Tuple<Tuple<String, String>, String>> parseArgs(ArgumentParser parser, Locale locale) {
		String target = "en";
		if (!parser.couldBeQuote()) {
			//TODO: Detect proper language
		}
		
		if (!parser.couldBeQuote()) {
			return expect(locale, parser, "command.error.quote");
		}
		String text = parser.eatQuote();
		
		return Mono.just(Tuple.of(Tuple.of(null, target), text));
	}
	
	private Mono<Void> runCommand(Channel channel, String type, String target, String text, UserID author, Locale locale) {
		@SuppressWarnings("deprecation")
		String key = channel.getClient().getBotEngine().getConfiguration().getCustomField("yandex-key");
		return TranslateUtil.translate(key, text, target)
			.map(resp->String.format("Translation: %s\n(Powered by Yandex Translate (<https://translate.yandex.com/>)) (Invoked by user ID: %s)",
				resp.result, author.getLong())) //TODO: Localize
			.flatMap(ftext->channel.getInterface(ChannelTextInterface.class).send(ftext))
			.then();
	}
}
