package everyos.discord.luwu.command.moderation;

import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.GuildMessageChannel;
import discord4j.rest.util.Permission;
import everyos.discord.luwu.adapter.GuildAdapter;
import everyos.discord.luwu.annotation.Help;
import everyos.discord.luwu.command.CategoryEnum;
import everyos.discord.luwu.command.CommandData;
import everyos.discord.luwu.command.ICommand;
import everyos.discord.luwu.database.DBObject;
import everyos.discord.luwu.localization.LocalizedString;
import everyos.discord.luwu.parser.ArgumentParser;
import everyos.discord.luwu.util.BotPermissionUtil;
import everyos.discord.luwu.util.ErrorUtil.LocalizedException;
import everyos.discord.luwu.util.PermissionUtil;
import reactor.core.publisher.Mono;

@Help(help=LocalizedString.UnmuteCommandHelp, ehelp = LocalizedString.UnmuteCommandExtendedHelp, category=CategoryEnum.Moderation)
public class UnmuteCommand implements ICommand {
	@Override public Mono<?> execute(Message message, CommandData data, String argument) {
		return message.getChannel().cast(GuildMessageChannel.class)
			.flatMap(channel->BotPermissionUtil.check(channel, new Permission[] {Permission.MANAGE_ROLES}).then(Mono.just(channel)))
			.flatMap(channel->{
				ArgumentParser parser = new ArgumentParser(argument);
				if (!parser.couldBeUserID()) return Mono.error(new LocalizedException(LocalizedString.UnrecognizedUsage));
				return message.getAuthorAsMember()
					.flatMap(member->PermissionUtil.check(member, new Permission[] {Permission.MANAGE_ROLES}, new Permission[] {Permission.MANAGE_MESSAGES}))
					.then(message.getGuild()).flatMap(guild->{
						return GuildAdapter.of(data.bot, guild.getId().asLong()).getDocument().flatMap(doc->{
							DBObject obj = doc.getObject();
							if (!obj.has("muteid")) return Mono.empty();
							return Mono.just(Snowflake.of(obj.getOrDefaultLong("muteid", -1L))); 
						}).flatMap(role->{
							return guild.getMemberById(Snowflake.of(parser.eatUserID()))
								.flatMap(member->member.removeRole(role));
						}).then(channel.createMessage(data.localize(LocalizedString.MemberUnmuted)));
					});
			});
	}
}
