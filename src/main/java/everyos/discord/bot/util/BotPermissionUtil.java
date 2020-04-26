package everyos.discord.bot.util;

import discord4j.core.object.entity.channel.Channel;
import discord4j.core.object.entity.channel.GuildChannel;
import discord4j.rest.util.Permission;
import everyos.discord.bot.localization.LocalizedString;
import everyos.discord.bot.util.ErrorUtil.LocalizedException;
import reactor.core.publisher.Mono;

public class BotPermissionUtil {
	public static Mono<Void> check(Channel channel, Permission[]... permissionsl) {
		if (!(channel instanceof GuildChannel)) return Mono.empty();
		return ((GuildChannel) channel).getGuild().flatMap(guild->{
			return guild.getClient().getSelfId()
				.flatMap(id->guild.getMemberById(id))
				.flatMap(member->{
					return member.getBasePermissions().flatMap(perms->{
						if (perms.contains(Permission.ADMINISTRATOR)) return Mono.empty();
						boolean hasPerms = false;
						Permission missing = null;
						for (Permission[] permissions:permissionsl) {
							hasPerms = true;
							for (Permission perm: permissions) {
								if (!perms.contains(perm)) {
									missing = perm;
									hasPerms = false; break;
								}
							}
							if (hasPerms) break;
						}
						if (!hasPerms) return Mono.error(new LocalizedException(
							LocalizedString.InsufficientBotPermissions,
							FillinUtil.of("permission", missing.toString())));
						
						return Mono.empty();
					});
				});
		});
	}
	
	public static Mono<Void> check(Channel channel, Permission... permissions) {
		return check(channel, new Permission[][] {permissions});
	}
}
