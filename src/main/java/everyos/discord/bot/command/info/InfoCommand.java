package everyos.discord.bot.command.info;

import discord4j.core.object.entity.Message;
import everyos.discord.bot.annotation.Help;
import everyos.discord.bot.command.CategoryEnum;
import everyos.discord.bot.command.CommandData;
import everyos.discord.bot.command.ICommand;
import everyos.discord.bot.localization.LocalizedString;
import reactor.core.publisher.Mono;

@Help(help=LocalizedString.InfoCommandHelp, ehelp = LocalizedString.InfoCommandExtendedHelp, category=CategoryEnum.Info)
public class InfoCommand implements ICommand {
	@Override public Mono<?> execute(Message message, CommandData data, String argument) {
		return message.getChannel()
			.flatMap(channel->channel.createEmbed(embed->{
				embed.setTitle(data.localize(LocalizedString.Info));
				embed.setDescription(data.localize(LocalizedString.InfoDescription));
			}));
	}
}
