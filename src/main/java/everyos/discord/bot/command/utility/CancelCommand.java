package everyos.discord.bot.command.utility;

import discord4j.core.object.entity.Message;
import everyos.discord.bot.adapter.ChannelUserAdapter;
import everyos.discord.bot.annotation.Help;
import everyos.discord.bot.command.CategoryEnum;
import everyos.discord.bot.command.CommandData;
import everyos.discord.bot.command.ICommand;
import everyos.discord.bot.localization.LocalizedString;
import reactor.core.publisher.Mono;

@Help(help=LocalizedString.CancelCommandHelp, ehelp = LocalizedString.CancelCommandExtendedHelp, category=CategoryEnum.Utility)
public class CancelCommand implements ICommand {
	@Override public Mono<?> execute(Message message, CommandData data, String argument) {
		return message.getChannel().flatMap(channel->{
			ChannelUserAdapter.of(data.shard, channel, message.getAuthor().get()).getData((obj, doc)->{
				obj.remove("data"); obj.remove("type");
			});
			return channel.createMessage(data.localize(LocalizedString.ActionCancelled));
		});
	}
}
