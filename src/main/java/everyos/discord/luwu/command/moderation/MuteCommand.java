package everyos.discord.luwu.command.moderation;

import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.Role;
import discord4j.core.object.entity.channel.GuildMessageChannel;
import discord4j.rest.util.Permission;
import discord4j.rest.util.PermissionSet;
import everyos.discord.luwu.BotInstance;
import everyos.discord.luwu.adapter.GuildAdapter;
import everyos.discord.luwu.annotation.Help;
import everyos.discord.luwu.command.CategoryEnum;
import everyos.discord.luwu.command.CommandData;
import everyos.discord.luwu.command.ICommand;
import everyos.discord.luwu.database.DBObject;
import everyos.discord.luwu.localization.LocalizedString;
import everyos.discord.luwu.parser.ArgumentParser;
import everyos.discord.luwu.util.ErrorUtil.LocalizedException;
import everyos.discord.luwu.util.BotPermissionUtil;
import everyos.discord.luwu.util.PermissionUtil;
import reactor.core.publisher.Mono;

@Help(help=LocalizedString.MuteCommandHelp, ehelp = LocalizedString.MuteCommandExtendedHelp, category=CategoryEnum.Moderation)
public class MuteCommand implements ICommand {
	//TODO: Add timed mute
	@Override public Mono<?> execute(Message message, CommandData data, String argument) {
		
		return message.getChannel().cast(GuildMessageChannel.class).flatMap(channel->{
			ArgumentParser parser = new ArgumentParser(argument);
			if (!parser.couldBeUserID()) return Mono.error(new LocalizedException(LocalizedString.UnrecognizedUsage));
			long uid = parser.eatUserID();
			return BotPermissionUtil.check(channel, new Permission[] {Permission.MANAGE_ROLES})
				.then(message.getAuthorAsMember())
				.flatMap(member->PermissionUtil.check(member, new Permission[] {Permission.MANAGE_ROLES}, new Permission[] {Permission.MANAGE_MESSAGES}))
				.flatMap(member->PermissionUtil.checkHigherThan(member, uid))
				.then(message.getGuild()).flatMap(guild->{
					return GuildAdapter.of(data.bot, guild.getId().asLong()).getDocument().flatMap(doc->{
						DBObject obj = doc.getObject();
						if (obj.has("muteid")) {
							return guild.getRoleById(Snowflake.of(obj.getOrDefaultLong("muteid", -1L)))
								.onErrorResume(e->createRole(data.bot, guild));
						} else return createRole(data.bot, guild);
					}).flatMap(role->{
						return guild.getMemberById(Snowflake.of(uid))
							.flatMap(member->member.addRole(role.getId()))
							.then(channel.createMessage(data.localize(LocalizedString.MemberMuted)));
					});
				});
		});
	}
	
	private Mono<Role> createRole(BotInstance bot, Guild guild) {
		return guild.createRole(role->{
			role.setName("Muted");
			role.setPermissions(PermissionSet.none());
		}).flatMap(role->{
			return GuildAdapter.of(bot, guild.getId().asLong()).getDocument().flatMap(doc->{
				doc.getObject().set("muteid", role.getId().asLong());
				return doc.save().then(Mono.just(role));
			});
		});
	}
}
