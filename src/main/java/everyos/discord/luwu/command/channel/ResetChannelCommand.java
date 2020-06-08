package everyos.discord.luwu.command.channel;

import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Message;
import discord4j.rest.util.Permission;
import everyos.discord.luwu.adapter.ChannelAdapter;
import everyos.discord.luwu.annotation.Help;
import everyos.discord.luwu.command.CategoryEnum;
import everyos.discord.luwu.command.CommandData;
import everyos.discord.luwu.command.ICommand;
import everyos.discord.luwu.database.DBObject;
import everyos.discord.luwu.localization.LocalizedString;
import everyos.discord.luwu.parser.ArgumentParser;
import everyos.discord.luwu.util.ErrorUtil.LocalizedException;
import everyos.discord.luwu.util.PermissionUtil;
import reactor.core.publisher.Mono;

@Help(category=CategoryEnum.Channel)
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
