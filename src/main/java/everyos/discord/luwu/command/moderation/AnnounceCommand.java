package everyos.discord.luwu.command.moderation;

import discord4j.core.object.entity.Message;
import everyos.discord.luwu.annotation.Help;
import everyos.discord.luwu.command.CategoryEnum;
import everyos.discord.luwu.command.CommandData;
import everyos.discord.luwu.command.ICommand;
import everyos.discord.luwu.localization.LocalizedString;
import reactor.core.publisher.Mono;

@Help(help=LocalizedString.AnnounceCommandHelp, ehelp = LocalizedString.AnnounceCommandExtendedHelp, category=CategoryEnum.Moderation)
public class AnnounceCommand implements ICommand {
	@Override public Mono<?> execute(Message message, CommandData data, String argument) {
		return Mono.empty();
	}
}
