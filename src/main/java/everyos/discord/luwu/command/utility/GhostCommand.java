package everyos.discord.luwu.command.utility;

import discord4j.core.object.entity.Message;
import discord4j.rest.util.Permission;
import everyos.discord.luwu.annotation.Help;
import everyos.discord.luwu.command.CategoryEnum;
import everyos.discord.luwu.command.CommandData;
import everyos.discord.luwu.command.ICommand;
import everyos.discord.luwu.localization.LocalizedString;
import everyos.discord.luwu.util.PermissionUtil;
import reactor.core.publisher.Mono;

@Help(help=LocalizedString.GhostCommandHelp, ehelp=LocalizedString.GhostCommandExtendedHelp, category=CategoryEnum.Utility)
public class GhostCommand implements ICommand {
	@Override public Mono<?> execute(Message message, CommandData data, String argument) {
		return message.getAuthorAsMember()
			.flatMap(member->PermissionUtil.check(member, Permission.MANAGE_ROLES))
			.then(message.delete());
	}
}
