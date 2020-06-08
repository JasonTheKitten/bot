package everyos.discord.luwu.command.utility;

import discord4j.core.object.entity.Message;
import everyos.discord.luwu.adapter.ChannelUserAdapter;
import everyos.discord.luwu.annotation.Help;
import everyos.discord.luwu.command.CategoryEnum;
import everyos.discord.luwu.command.CommandData;
import everyos.discord.luwu.command.ICommand;
import everyos.discord.luwu.database.DBObject;
import everyos.discord.luwu.localization.LocalizedString;
import reactor.core.publisher.Mono;

@Help(help=LocalizedString.CancelCommandHelp, ehelp = LocalizedString.CancelCommandExtendedHelp, category=CategoryEnum.Utility)
public class CancelCommand implements ICommand {
	@Override public Mono<?> execute(Message message, CommandData data, String argument) {
		return message.getChannel().flatMap(channel->{
			return ChannelUserAdapter.of(data.bot, channel, message.getAuthor().get()).getDocument().flatMap(doc->{
				DBObject obj = doc.getObject();
				obj.remove("data"); obj.remove("type");
				
				return channel.createMessage(data.localize(LocalizedString.ActionCancelled));
			});
		});
	}
}
