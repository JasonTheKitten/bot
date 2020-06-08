package everyos.discord.luwu.command.utility;

import discord4j.core.object.entity.Message;
import everyos.discord.luwu.annotation.Help;
import everyos.discord.luwu.command.CategoryEnum;
import everyos.discord.luwu.command.CommandData;
import everyos.discord.luwu.command.ICommand;
import everyos.discord.luwu.localization.LocalizedString;
import everyos.discord.luwu.parser.ArgumentParser;
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
