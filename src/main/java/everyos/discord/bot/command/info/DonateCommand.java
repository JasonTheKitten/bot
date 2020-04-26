package everyos.discord.bot.command.info;

import discord4j.core.object.entity.Message;
import everyos.discord.bot.annotation.Help;
import everyos.discord.bot.command.CategoryEnum;
import everyos.discord.bot.command.CommandData;
import everyos.discord.bot.command.ICommand;
import everyos.discord.bot.localization.LocalizedString;
import everyos.discord.bot.util.FillinUtil;
import reactor.core.publisher.Mono;

@Help(help=LocalizedString.DonateCommandHelp, ehelp = LocalizedString.DonateCommandExtendedHelp, category=CategoryEnum.Info)
public class DonateCommand implements ICommand {
	@Override public Mono<?> execute(Message message, CommandData data, String argument) {
		return message.getChannel().flatMap(channel->
			channel.createMessage(data.localize(LocalizedString.Donate, FillinUtil.of("url", "https://shorturl.at/fgmv1"))));
	}
}
