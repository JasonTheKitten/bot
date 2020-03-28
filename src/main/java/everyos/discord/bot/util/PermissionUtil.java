package everyos.discord.bot.util;

import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.MessageChannel;
import discord4j.core.object.util.Permission;
import everyos.discord.bot.localization.LocalizationProvider;
import everyos.discord.bot.localization.LocalizedString;
import reactor.core.publisher.Mono;

public class PermissionUtil {
	public static Mono<?> check(Member member, MessageChannel channel, LocalizationProvider locale, Permission[]... permissionsl) {
		return member.getBasePermissions().flatMap(perms->{
			if (perms.contains(Permission.ADMINISTRATOR)) return Mono.just(member);
			boolean hasPerms = false;
			for (Permission[] permissions:permissionsl) {
				hasPerms = true;
				for (Permission perm: permissions) {
					if (!perms.contains(perm)) {//TODO: Also use saved guild data
						hasPerms = false; break;
					}
				}
				if (hasPerms) break;
			}
			if (!hasPerms) return channel.createMessage(locale.localize(LocalizedString.InsufficientPermissions)).then(Mono.empty());
			
			return Mono.just(member);
		});
	}
	
	public static Mono<?> check(Member member, MessageChannel channel, LocalizationProvider locale, Permission... permissions) {
		return check(member, channel, locale, new Permission[][] {permissions});
	}
}
