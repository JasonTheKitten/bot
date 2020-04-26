package everyos.discord.bot.command.utility;

import discord4j.core.object.entity.Message;
import everyos.discord.bot.annotation.Help;
import everyos.discord.bot.command.CategoryEnum;
import everyos.discord.bot.command.CommandData;
import everyos.discord.bot.command.ICommand;
import everyos.discord.bot.localization.LocalizedString;
import reactor.core.publisher.Mono;

@Help(help=LocalizedString.RemindIntervalCommandHelp, ehelp = LocalizedString.RemindIntervalCommandExtendedHelp, category=CategoryEnum.Utility)
public class RemindIntervalCommand implements ICommand {
	@Override
	public Mono<?> execute(Message message, CommandData data, String argument) {
		return null;
	}
}
