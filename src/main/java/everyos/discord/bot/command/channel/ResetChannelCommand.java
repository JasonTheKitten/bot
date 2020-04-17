package everyos.discord.bot.command.channel;

import discord4j.core.object.entity.Message;
import discord4j.rest.util.Permission;
import discord4j.rest.util.Snowflake;
import everyos.discord.bot.adapter.ChannelAdapter;
import everyos.discord.bot.command.CommandData;
import everyos.discord.bot.command.ICommand;
import everyos.discord.bot.localization.LocalizedString;
import everyos.discord.bot.parser.ArgumentParser;
import everyos.discord.bot.util.ErrorUtil.LocalizedException;
import everyos.discord.bot.util.PermissionUtil;
import reactor.core.publisher.Mono;

//@Ignorable(id=6)
public class ResetChannelCommand implements ICommand {
	@Override public Mono<?> execute(Message message, CommandData data, String argument) {
		return message.getAuthorAsMember()
			.flatMap(member->PermissionUtil.check(member, Permission.MANAGE_CHANNELS))
			.then(message.getChannel()).flatMap(channel->{
				
			ArgumentParser parser = new ArgumentParser(argument);
			if (!parser.couldBeChannelID()) return Mono.error(new LocalizedException(LocalizedString.UnrecognizedChannel));
				return message.getGuild().flatMap(guild->{
					long cID = parser.eatChannelID();
					return guild.getChannelById(Snowflake.of(cID)).doOnNext(o->clean(ChannelAdapter.of(data.shard, cID)))
						.then(channel.createMessage(data.localize(LocalizedString.ConfigurationReset)));
				});
			});
	}
	
	public void clean(ChannelAdapter channel) {
		channel.getData((obj, doc)->{
			obj.remove("type");
			obj.remove("data");
			
			doc.save();
			//TODO: Remove chatlink info and the likes
		});
	}
}
