package everyos.discord.bot.command.utility;

import discord4j.core.object.entity.Message;
import everyos.discord.bot.annotation.Help;
import everyos.discord.bot.command.CategoryEnum;
import everyos.discord.bot.command.CommandData;
import everyos.discord.bot.command.ICommand;
import everyos.discord.bot.localization.LocalizedString;
import everyos.discord.bot.parser.ArgumentParser;
import reactor.core.publisher.Mono;

@Help(help=LocalizedString.RemindCommandHelp, ehelp = LocalizedString.RemindCommandExtendedHelp, category=CategoryEnum.Utility)
public class RemindCommand implements ICommand {
	@Override public Mono<?> execute(Message message, CommandData data, String argument) {
		return message.getChannel().flatMap(channel->{
			ArgumentParser parser = new ArgumentParser(argument);
			
			return null;
		});
	}
}
