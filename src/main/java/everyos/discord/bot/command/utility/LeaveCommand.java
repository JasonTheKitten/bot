package everyos.discord.bot.command.utility;

import discord4j.core.object.entity.Message;
import discord4j.core.object.util.Permission;
import everyos.discord.bot.adapter.GuildAdapter;
import everyos.discord.bot.command.CommandData;
import everyos.discord.bot.command.ICommand;
import everyos.discord.bot.localization.LocalizedString;
import everyos.discord.bot.parser.ArgumentParser;
import everyos.discord.bot.util.PermissionUtil;
import reactor.core.publisher.Mono;

public class LeaveCommand implements ICommand {
	@Override public Mono<?> execute(Message message, CommandData data, String argument) {
		return message.getChannel().flatMap(channel->{
			return message.getAuthorAsMember()
				.flatMap(m->PermissionUtil.check(m, channel, data.locale, 
					new Permission[] {Permission.MANAGE_CHANNELS},
					new Permission[] {Permission.MANAGE_MESSAGES}))
				.flatMap(o->message.getGuild())
				.flatMap(guild->{
					ArgumentParser parser = new ArgumentParser(argument);
					
					if (parser.isEmpty()) {
						GuildAdapter.of(data.shard, guild).getDocument().getObject((obj, doc)->{
							obj.remove("lmsgc");
							obj.remove("lmsg");
							
							doc.save();
						});
						
						return channel.createMessage(data.localize(LocalizedString.ConfigurationReset));
					}
					
					if (!parser.couldBeChannelID()) 
						return channel.createMessage(data.localize(LocalizedString.UnrecognizedUsage));
					
					String wchannel = parser.eatChannelID();
					if (parser.isEmpty()) return channel.createMessage(data.localize(LocalizedString.UnrecognizedUsage));
					String wmessage = parser.toString();
					
					GuildAdapter.of(data.shard, guild).getDocument().getObject((obj, doc)->{
						obj.set("lmsgc", wchannel);
						obj.set("lmsg", wmessage);
						
						doc.save();
					});
					
					return channel.createMessage(data.localize(LocalizedString.LeaveMessageSet));
				});
		});
	}
}
