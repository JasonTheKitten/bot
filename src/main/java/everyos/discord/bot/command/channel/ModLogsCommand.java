package everyos.discord.bot.command.channel;

import discord4j.core.object.entity.Message;
import discord4j.rest.util.Permission;
import everyos.discord.bot.annotation.Ignorable;
import everyos.discord.bot.command.CommandData;
import everyos.discord.bot.command.ICommand;
import everyos.discord.bot.localization.LocalizedString;
import everyos.discord.bot.parser.ArgumentParser;
import everyos.discord.bot.util.PermissionUtil;
import reactor.core.publisher.Mono;

@Ignorable(id=4)
public class ModLogsCommand implements ICommand {
	@Override public Mono<?> execute(Message message, CommandData data, String argument) {
		return message.getAuthorAsMember().flatMap(member->PermissionUtil.check(member, 
			new Permission[]{Permission.MANAGE_CHANNELS},
			new Permission[]{Permission.MANAGE_MESSAGES}))
		.then(message.getChannel()).flatMap(channel->{
			ArgumentParser parser = new ArgumentParser(argument);
			if (!parser.couldBeChannelID()) return channel.createMessage(data.localize(LocalizedString.UnrecognizedUsage));
			long channelid = parser.eatChannelID();
			return message.getGuild().flatMap(guild->{
				
				
				return null;
			});
		});
	}
}
