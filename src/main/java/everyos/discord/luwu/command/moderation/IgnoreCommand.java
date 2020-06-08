package everyos.discord.luwu.command.moderation;

import discord4j.core.object.entity.Message;
import discord4j.rest.util.Permission;
import everyos.discord.luwu.adapter.GuildAdapter;
import everyos.discord.luwu.adapter.ModMemberAdapter;
import everyos.discord.luwu.annotation.Help;
import everyos.discord.luwu.command.CategoryEnum;
import everyos.discord.luwu.command.CommandData;
import everyos.discord.luwu.command.ICommand;
import everyos.discord.luwu.localization.LocalizedString;
import everyos.discord.luwu.parser.ArgumentParser;
import everyos.discord.luwu.util.PermissionUtil;
import everyos.discord.luwu.util.ErrorUtil.LocalizedException;
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
