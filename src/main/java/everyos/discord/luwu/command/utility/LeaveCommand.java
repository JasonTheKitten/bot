package everyos.discord.luwu.command.utility;

import discord4j.core.object.entity.Message;
import discord4j.rest.util.Permission;
import everyos.discord.luwu.adapter.GuildAdapter;
import everyos.discord.luwu.annotation.Help;
import everyos.discord.luwu.command.CategoryEnum;
import everyos.discord.luwu.command.CommandData;
import everyos.discord.luwu.command.ICommand;
import everyos.discord.luwu.database.DBObject;
import everyos.discord.luwu.localization.LocalizedString;
import everyos.discord.luwu.parser.ArgumentParser;
import everyos.discord.luwu.util.PermissionUtil;
import reactor.core.publisher.Mono;

@Help(help=LocalizedString.LMSGCommandHelp, ehelp = LocalizedString.LMSGCommandExtendedHelp, category=CategoryEnum.Utility)
public class LeaveCommand implements ICommand {
	@Override public Mono<?> execute(Message message, CommandData data, String argument) {
		return message.getChannel().flatMap(channel->{
			return message.getAuthorAsMember()
				.flatMap(m->PermissionUtil.check(m,
					new Permission[] {Permission.MANAGE_CHANNELS},
					new Permission[] {Permission.MANAGE_MESSAGES}))
				.flatMap(o->message.getGuild())
				.flatMap(guild->{
					ArgumentParser parser = new ArgumentParser(argument);
					
					if (parser.isEmpty()) {
						GuildAdapter.of(data.bot, guild).getDocument().flatMap(doc->{
							DBObject obj = doc.getObject();
							obj.remove("lmsgc");
							obj.remove("lmsg");
							
							return doc.save();
						}).then(channel.createMessage(data.localize(LocalizedString.ConfigurationReset)));
					}
					
					if (!parser.couldBeChannelID()) 
						return channel.createMessage(data.localize(LocalizedString.UnrecognizedUsage));
					
					long wchannel = parser.eatChannelID();
					if (parser.isEmpty()) return channel.createMessage(data.localize(LocalizedString.UnrecognizedUsage));
					String wmessage = parser.toString();
					
					return GuildAdapter.of(data.bot, guild).getDocument().flatMap(doc->{
						DBObject obj = doc.getObject();
						obj.set("lmsgc", wchannel);
						obj.set("lmsg", wmessage);
						
						return doc.save();
					}).then(channel.createMessage(data.localize(LocalizedString.LeaveMessageSet)));
				});
		});
	}
}
