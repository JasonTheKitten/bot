package everyos.discord.bot.command.moderation;

import discord4j.core.object.entity.Message;
import everyos.discord.bot.annotation.Help;
import everyos.discord.bot.command.CategoryEnum;
import everyos.discord.bot.command.CommandData;
import everyos.discord.bot.command.ICommand;
import everyos.discord.bot.localization.LocalizedString;
import reactor.core.publisher.Mono;

@Help(help=LocalizedString.AnnounceCommandHelp, ehelp = LocalizedString.AnnounceCommandExtendedHelp, category=CategoryEnum.Moderation)
public class AnnounceCommand implements ICommand {
	@Override public Mono<?> execute(Message message, CommandData data, String argument) {
		return Mono.empty();
	}
}
