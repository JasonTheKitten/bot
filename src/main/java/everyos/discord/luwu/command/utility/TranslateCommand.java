package everyos.discord.luwu.command.utility;

import discord4j.core.object.entity.Message;
import everyos.discord.luwu.annotation.Help;
import everyos.discord.luwu.command.CategoryEnum;
import everyos.discord.luwu.command.CommandData;
import everyos.discord.luwu.command.ICommand;
import everyos.discord.luwu.localization.LocalizedString;
import everyos.discord.luwu.util.TranslateUtil;
import reactor.core.publisher.Mono;

@Help(help=LocalizedString.TranslateCommandHelp, ehelp = LocalizedString.TranslateCommandExtendedHelp, category=CategoryEnum.Utility)
public class TranslateCommand implements ICommand {
	@Override public Mono<?> execute(Message message, CommandData data, String argument) {
		return message.getChannel().flatMap(channel->{ //TODO: Support other languages
			return TranslateUtil.translate(data.bot.yandexKey, argument, TranslateUtil.locale(data.locale.locale))
				.flatMap(resp->channel.createMessage(data.safe(
					String.format("Translation: %s\n(Powered by Yandex Translate ( <https://translate.yandex.com/> )) (Invoked by user ID: %s)",
						resp.result, message.getAuthor().get().getId().asLong()))));//TODO: Localize
		});
	}
}
