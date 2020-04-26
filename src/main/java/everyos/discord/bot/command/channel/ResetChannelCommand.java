package everyos.discord.bot.command.channel;

import discord4j.core.object.entity.Message;
import discord4j.rest.util.Permission;
import discord4j.rest.util.Snowflake;
import everyos.discord.bot.adapter.ChannelAdapter;
import everyos.discord.bot.command.CommandData;
import everyos.discord.bot.command.ICommand;
import everyos.discord.bot.database.DBObject;
import everyos.discord.bot.localization.LocalizedString;
import everyos.discord.bot.parser.ArgumentParser;
import everyos.discord.bot.util.ErrorUtil.LocalizedException;
import everyos.discord.bot.util.PermissionUtil;
import reactor.core.publisher.Mono;

public class ResetChannelCommand implements ICommand {
	@Override public Mono<?> execute(Message message, CommandData data, String argument) {
		return message.getAuthorAsMember()
			.flatMap(member->PermissionUtil.check(member, Permission.MANAGE_CHANNELS))
			.then(message.getChannel()).flatMap(channel->{
				
			ArgumentParser parser = new ArgumentParser(argument);
			if (!parser.couldBeChannelID()) return Mono.error(new LocalizedException(LocalizedString.UnrecognizedChannel));
				return message.getGuild().flatMap(guild->{
					long cID = parser.eatChannelID();
					return guild.getChannelById(Snowflake.of(cID)).flatMap(o->clean(ChannelAdapter.of(data.bot, cID)))
						.then(channel.createMessage(data.localize(LocalizedString.ConfigurationReset)));
				});
			});
	}
	
	public Mono<Void> clean(ChannelAdapter channel) {
		return channel.getDocument().flatMap(doc->{
			DBObject obj = doc.getObject();
			obj.remove("type");
			obj.remove("data");
			
			return doc.save();
			//TODO: Remove chatlink info and the likes
		});
	}
}
