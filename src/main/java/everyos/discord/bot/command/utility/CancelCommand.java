package everyos.discord.bot.command.utility;

import discord4j.core.object.entity.Message;
import everyos.discord.bot.adapter.ChannelUserAdapter;
import everyos.discord.bot.command.CommandData;
import everyos.discord.bot.command.ICommand;
import everyos.discord.bot.localization.LocalizedString;
import reactor.core.publisher.Mono;

public class CancelCommand implements ICommand {
	@Override public Mono<?> execute(Message message, CommandData data, String argument) {
		return message.getChannel().flatMap(channel->{
			ChannelUserAdapter.of(data.shard, channel, message.getAuthor().get()).getDocument().getObject((obj, doc)->{
				obj.remove("data"); obj.remove("type");
			});
			return channel.createMessage(data.localize(LocalizedString.ActionCancelled));
		});
	}
}
