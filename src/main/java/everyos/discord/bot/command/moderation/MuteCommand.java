package everyos.discord.bot.command.moderation;

import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.Role;
import discord4j.core.object.entity.channel.GuildMessageChannel;
import discord4j.rest.util.Permission;
import discord4j.rest.util.PermissionSet;
import discord4j.rest.util.Snowflake;
import everyos.discord.bot.ShardInstance;
import everyos.discord.bot.adapter.GuildAdapter;
import everyos.discord.bot.annotation.Help;
import everyos.discord.bot.command.CategoryEnum;
import everyos.discord.bot.command.CommandData;
import everyos.discord.bot.command.ICommand;
import everyos.discord.bot.localization.LocalizedString;
import everyos.discord.bot.parser.ArgumentParser;
import everyos.discord.bot.util.ErrorUtil.LocalizedException;
import everyos.discord.bot.util.PermissionUtil;
import reactor.core.publisher.Mono;

@Help(help=LocalizedString.MuteCommandHelp, ehelp = LocalizedString.MuteCommandExtendedHelp, category=CategoryEnum.Moderation)
public class MuteCommand implements ICommand {
	//TODO: Add timed mute
	//TODO: Check member heirchy
	@Override public Mono<?> execute(Message message, CommandData data, String argument) {
		return message.getChannel().cast(GuildMessageChannel.class).flatMap(channel->{
			ArgumentParser parser = new ArgumentParser(argument);
			if (!parser.couldBeUserID()) return Mono.error(new LocalizedException(LocalizedString.UnrecognizedUsage));
			return message.getAuthorAsMember()
				.flatMap(member->PermissionUtil.check(member, new Permission[] {Permission.MANAGE_ROLES}, new Permission[] {Permission.MANAGE_MESSAGES}))
				.then(message.getGuild()).flatMap(guild->{
					return GuildAdapter.of(data.shard, guild.getId().asLong()).getData(obj->{
						if (obj.has("muteid")) {
							return guild.getRoleById(Snowflake.of(obj.getOrDefaultString("muteid", null)))
								.onErrorResume(e->createRole(data.shard, guild));
						} else return createRole(data.shard, guild);
					}).flatMap(role->{
						return guild.getMemberById(Snowflake.of(parser.eatUserID()))
							.flatMap(member->member.addRole(role.getId()))
							.then(channel.createMessage(data.localize(LocalizedString.MemberMuted)));
					});
				});
		});
	}
	
	private Mono<Role> createRole(ShardInstance shard, Guild guild) {
		return guild.createRole(role->{
			role.setName("Muted");
			role.setPermissions(PermissionSet.none());
		}).doOnNext(role->{
			GuildAdapter.of(shard, guild.getId().asLong()).getData((obj, doc)->{
				obj.set("muteid", role.getId().asLong());
				doc.save();
			});
		});
	}
}
