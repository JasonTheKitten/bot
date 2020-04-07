package everyos.discord.bot.command.configuration;

import discord4j.core.object.entity.Message;
import everyos.discord.bot.annotation.Help;
import everyos.discord.bot.command.CategoryEnum;
import everyos.discord.bot.command.CommandData;
import everyos.discord.bot.command.ICommand;
import everyos.discord.bot.localization.LocalizedString;
import reactor.core.publisher.Mono;

@Help(help=LocalizedString.MessageCommandHelp, ehelp = LocalizedString.MessageCommandExtendedHelp, category=CategoryEnum.Configuration)
public class MessageCommand implements ICommand {
	@Override public Mono<?> execute(Message message, CommandData data, String argument) {
		return null; //Localization overrides will go here
	}
}
