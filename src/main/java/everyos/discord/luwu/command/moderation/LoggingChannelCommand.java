package everyos.discord.luwu.command.moderation;

import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.GuildMessageChannel;
import discord4j.rest.util.Permission;
import everyos.discord.luwu.adapter.GuildAdapter;
import everyos.discord.luwu.command.CommandData;
import everyos.discord.luwu.command.ICommand;
import everyos.discord.luwu.localization.LocalizedString;
import everyos.discord.luwu.parser.ArgumentParser;
import everyos.discord.luwu.util.PermissionUtil;
import reactor.core.publisher.Mono;

public class LoggingChannelCommand implements ICommand {
	@Override public Mono<?> execute(Message message, CommandData data, String argument) {
		return message.getAuthorAsMember().flatMap(member->PermissionUtil.check(member, Permission.MANAGE_CHANNELS))
			.then(message.getChannel().cast(GuildMessageChannel.class)).flatMap(channel->{
				ArgumentParser parser = new ArgumentParser(argument);
				
				if (parser.isEmpty()) {
					return GuildAdapter.of(data.bot, channel.getGuildId().asLong()).getDocument().flatMap(doc->{
						doc.getObject().remove("logging");
						
						return doc.save().then(channel.createMessage(data.localize(LocalizedString.LogsChannelCleared)));
					});
				}
				
				if (!parser.couldBeChannelID()) return channel.createMessage(data.localize(LocalizedString.UnrecognizedUsage));
				long channelID = parser.eatChannelID();
				return GuildAdapter.of(data.bot, channel.getGuildId().asLong()).getDocument().flatMap(doc->{
					doc.getObject().set("logging", channelID);
					
					return doc.save().then(channel.createMessage(data.localize(LocalizedString.LogsChannelSet)));
				});
			});
	}
}
