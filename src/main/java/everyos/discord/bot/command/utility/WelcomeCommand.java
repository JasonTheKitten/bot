package everyos.discord.bot.command.utility;

import discord4j.core.object.entity.Message;
import discord4j.rest.util.Permission;
import everyos.discord.bot.adapter.GuildAdapter;
import everyos.discord.bot.annotation.Help;
import everyos.discord.bot.command.CategoryEnum;
import everyos.discord.bot.command.CommandData;
import everyos.discord.bot.command.ICommand;
import everyos.discord.bot.database.DBObject;
import everyos.discord.bot.localization.LocalizedString;
import everyos.discord.bot.parser.ArgumentParser;
import everyos.discord.bot.util.ErrorUtil.LocalizedException;
import everyos.discord.bot.util.PermissionUtil;
import reactor.core.publisher.Mono;

@Help(help=LocalizedString.WMSGCommandHelp, ehelp = LocalizedString.WMSGCommandExtendedHelp, category=CategoryEnum.Utility)
public class WelcomeCommand implements ICommand {
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
						return GuildAdapter.of(data.bot, guild).getDocument().flatMap(doc->{
							DBObject obj = doc.getObject();
							obj.remove("wmsgc");
							obj.remove("wmsg");
							
							return doc.save().then(channel.createMessage(data.localize(LocalizedString.ConfigurationReset)));
						});
					}
					
					if (!parser.couldBeChannelID()) 
						return Mono.error(new LocalizedException(LocalizedString.UnrecognizedUsage));
					
					long wchannel = parser.eatChannelID();
					if (parser.isEmpty()) return channel.createMessage(data.localize(LocalizedString.UnrecognizedUsage));
					String wmessage = parser.toString();
					
					return GuildAdapter.of(data.bot, guild).getDocument().flatMap(doc->{
						DBObject obj = doc.getObject();
						obj.set("wmsgc", wchannel);
						obj.set("wmsg", wmessage);
						
						return doc.save().then(channel.createMessage(data.localize(LocalizedString.WelcomeMessageSet)));
					});
				});
		});
	}
}