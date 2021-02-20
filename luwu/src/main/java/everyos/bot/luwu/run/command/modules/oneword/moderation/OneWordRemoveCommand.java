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

public class OneWordRemoveCommand extends CommandBase {

	public OneWordRemoveCommand() {
		super("command.oneword.moderation.remove", e->true,
			ChatPermission.SEND_MESSAGES,
			ChatPermission.MANAGE_MESSAGES);
	}

	@Override
	public Mono<Void> execute(CommandData data, ArgumentParser parser) {
		Locale locale = data.getLocale();
		
		return parseArgs(parser, locale)
			.flatMap(target->runCommand(data.getChannel(), target, locale));
	}

	private Mono<String> parseArgs(ArgumentParser parser, Locale locale) {
		if (!parser.couldBeQuote()) {
			return expect(locale, parser, "command.error.quoted");
		}
		return Mono.just(parser.eatQuote());
	}
	
	private Mono<Void> runCommand(Channel channel, String target, Locale locale) {
		return channel.as(OneWordChannel.type).flatMap(c->{
			return c.getInfo()
				.map(info->info.getMessage().replace(target, ""))
				.flatMap(newMessage->c.edit(spec->spec.setMessage(newMessage))
					.then(channel.getInterface(ChannelTextInterface.class).send("\u200e"+newMessage)));
		}).then();
	}
}
