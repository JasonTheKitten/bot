package everyos.discord.luwu.command.utility;

import discord4j.core.object.entity.Message;
import everyos.discord.luwu.annotation.Help;
import everyos.discord.luwu.command.CategoryEnum;
import everyos.discord.luwu.command.CommandData;
import everyos.discord.luwu.command.ICommand;
import everyos.discord.luwu.localization.LocalizedString;
import reactor.core.publisher.Mono;

@Help(help=LocalizedString.RemindIntervalCommandHelp, ehelp = LocalizedString.RemindIntervalCommandExtendedHelp, category=CategoryEnum.Utility)
public class RemindIntervalCommand implements ICommand {
	@Override
	public Mono<?> execute(Message message, CommandData data, String argument) {
		return null;
	}
}
