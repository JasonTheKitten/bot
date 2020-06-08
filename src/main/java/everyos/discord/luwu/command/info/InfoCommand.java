package everyos.discord.luwu.command.info;

import discord4j.core.object.entity.Message;
import everyos.discord.luwu.annotation.Help;
import everyos.discord.luwu.command.CategoryEnum;
import everyos.discord.luwu.command.CommandData;
import everyos.discord.luwu.command.ICommand;
import everyos.discord.luwu.localization.LocalizedString;
import everyos.discord.luwu.util.FillinUtil;
import reactor.core.publisher.Mono;

@Help(help=LocalizedString.InfoCommandHelp, ehelp = LocalizedString.InfoCommandExtendedHelp, category=CategoryEnum.Info)
public class InfoCommand implements ICommand {
	@Override public Mono<?> execute(Message message, CommandData data, String argument) {
		return message.getChannel()
			.flatMap(channel->channel.createEmbed(embed->{
				embed.setTitle(data.localize(LocalizedString.Info));
				embed.setDescription(data.localize(LocalizedString.InfoDescription, FillinUtil.of("invite_url","https://discordapp.com/oauth2/authorize?client_id=661817513247768588&scope=bot&permissions=338816087")));
			}));
	}
}
