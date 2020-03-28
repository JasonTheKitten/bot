package everyos.discord.bot.command.channel;

import discord4j.core.object.entity.Message;
import everyos.discord.bot.command.CommandData;
import everyos.discord.bot.command.ICommand;
import everyos.discord.bot.localization.LocalizedString;
import everyos.discord.bot.parser.ArgumentParser;
import reactor.core.publisher.Mono;

public class ModLogsCommand implements ICommand {
	@Override public Mono<?> execute(Message message, CommandData data, String argument) {
		return message.getChannel().flatMap(channel->{
			//TODO: Check permissions
			ArgumentParser parser = new ArgumentParser(argument);
			if (!parser.couldBeChannelID()) return channel.createMessage(data.localize(LocalizedString.UnrecognizedUsage));
			String channelid = parser.eatChannelID();
			return message.getGuild().flatMap(guild->{
				
				
				return null;
			});
		});
	}
}
