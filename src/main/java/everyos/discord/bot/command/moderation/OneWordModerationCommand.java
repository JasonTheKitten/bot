package everyos.discord.bot.command.moderation;

import java.util.HashMap;

import discord4j.core.object.entity.Message;
import everyos.discord.bot.annotation.Help;
import everyos.discord.bot.command.CategoryEnum;
import everyos.discord.bot.command.CommandData;
import everyos.discord.bot.command.ICommand;
import everyos.discord.bot.command.IGroupCommand;
import everyos.discord.bot.localization.Localization;
import everyos.discord.bot.localization.LocalizedString;
import reactor.core.publisher.Mono;

@Help(help=LocalizedString.OWMCommandHelp, ehelp = LocalizedString.OWMCommandExtendedHelp, category=CategoryEnum.Moderation)
public class OneWordModerationCommand implements IGroupCommand {
	@Override public Mono<?> execute(Message message, CommandData data, String argument) {
		return null;
	}
	
	@Override public HashMap<String, ICommand> getCommands(Localization locale) { return null; }
}
