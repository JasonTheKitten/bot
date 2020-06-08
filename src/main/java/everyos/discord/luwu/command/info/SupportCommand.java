package everyos.discord.luwu.command.info;

import discord4j.core.object.entity.Message;
import everyos.discord.luwu.annotation.Help;
import everyos.discord.luwu.command.CategoryEnum;
import everyos.discord.luwu.command.CommandData;
import everyos.discord.luwu.command.ICommand;
import everyos.discord.luwu.localization.LocalizedString;
import everyos.discord.luwu.util.FillinUtil;
import reactor.core.publisher.Mono;

@Help(help=LocalizedString.SupportCommandHelp, ehelp = LocalizedString.SupportCommandExtendedHelp, category=CategoryEnum.Info)
public class SupportCommand implements ICommand {
	@Override public Mono<?> execute(Message message, CommandData data, String argument) {
		return message.getChannel().flatMap(channel->
			channel.createMessage(data.localize(LocalizedString.SupportServer, FillinUtil.of("url", "https://discord.gg/Ux477Zm"))));
	}
}