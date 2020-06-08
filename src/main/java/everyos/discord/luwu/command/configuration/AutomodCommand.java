package everyos.discord.luwu.command.configuration;

import discord4j.core.object.entity.Message;
import everyos.discord.luwu.annotation.Help;
import everyos.discord.luwu.annotation.Ignorable;
import everyos.discord.luwu.command.CategoryEnum;
import everyos.discord.luwu.command.CommandData;
import everyos.discord.luwu.command.ICommand;
import everyos.discord.luwu.localization.LocalizedString;
import reactor.core.publisher.Mono;

@Ignorable(id=10)
@Help(help=LocalizedString.AutomodCommandHelp, ehelp = LocalizedString.AutomodCommandExtendedHelp, category=CategoryEnum.Configuration)
public class AutomodCommand implements ICommand {
	@Override public Mono<?> execute(Message message, CommandData data, String argument) {
		return null;
	}
}
