package everyos.discord.bot.command.moderation;

import discord4j.core.object.entity.Message;
import discord4j.rest.util.Permission;
import everyos.discord.bot.adapter.GuildAdapter;
import everyos.discord.bot.adapter.ModMemberAdapter;
import everyos.discord.bot.annotation.Help;
import everyos.discord.bot.command.CategoryEnum;
import everyos.discord.bot.command.CommandData;
import everyos.discord.bot.command.ICommand;
import everyos.discord.bot.localization.LocalizedString;
import everyos.discord.bot.parser.ArgumentParser;
import everyos.discord.bot.util.ErrorUtil.LocalizedException;
import everyos.discord.bot.util.PermissionUtil;
import reactor.core.publisher.Mono;

@Help(help=LocalizedString.IgnoreCommandHelp, ehelp = LocalizedString.IgnoreCommandExtendedHelp, category=CategoryEnum.Moderation)
public class IgnoreCommand implements ICommand {
	@Override public Mono<?> execute(Message message, CommandData data, String argument) {
		return message.getChannel().flatMap(channel->{
			return message.getAuthorAsMember()
				.flatMap(member->PermissionUtil.check(member, Permission.MANAGE_ROLES))//TODO: Higher than
				.flatMap(member->{
					ArgumentParser parser = new ArgumentParser(argument);
					if (!parser.couldBeUserID()) return Mono.error(new LocalizedException(LocalizedString.UnrecognizedUsage));
					long uid = parser.eatUserID();
					return
						PermissionUtil.checkHigherThan(member, uid)
						.then(message.getGuild())
						.flatMap(guild->ModMemberAdapter.of(GuildAdapter.of(data.bot, guild), uid).getDocument())
						.flatMap(doc->{
							doc.getObject().set("ignored", true);
							return doc.save().then(channel.createMessage(data.localize(LocalizedString.MemberIgnored)));
						});
				});
		});
	}
}
