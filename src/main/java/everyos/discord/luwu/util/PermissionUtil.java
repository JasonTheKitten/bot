package everyos.discord.luwu.util;

import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Member;
import discord4j.rest.util.Permission;
import everyos.discord.luwu.localization.LocalizedString;
import everyos.discord.luwu.util.ErrorUtil.LocalizedException;
import reactor.core.publisher.Mono;

public class PermissionUtil {
	public static Mono<Member> check(Member member, Permission[]... permissionsl) {
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
			if (!hasPerms) return Mono.error(new LocalizedException(LocalizedString.InsufficientPermissions));
			
			return Mono.just(member);
		});
	}
	
	public static Mono<Member> check(Member member, Permission... permissions) {
		return check(member, new Permission[][] {permissions});
	}
	
	public static Mono<Member> checkHigherThan(Member member1, Member member2) {
		return checkHigherThan(member1, member2.getId().asLong());
	}
	public static Mono<Member> checkHigherThan(Member member1, long member2) {
		return member1.isHigher(Snowflake.of(member2)).flatMap(higher->{
			if (!higher) return Mono.error(new LocalizedException(LocalizedString.MemberMustBeHigher));
			return Mono.just(member1);
		}).onErrorResume(e->{
			if (e instanceof LocalizedException) return Mono.error(e);
			return Mono.error(new LocalizedException(LocalizedString.UnrecognizedUser));
		});
	}
}
