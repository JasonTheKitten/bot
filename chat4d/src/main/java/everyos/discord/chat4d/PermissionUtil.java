package everyos.discord.chat4d;

import java.util.HashSet;
import java.util.Set;

import discord4j.rest.util.Permission;
import discord4j.rest.util.PermissionSet;

public final class PermissionUtil {
	private PermissionUtil() {}
	
	public static int fromNativePermissions(Set<Permission> perms) {
		if (perms.contains(Permission.ADMINISTRATOR)) {
			return Integer.MAX_VALUE;
		}
		
		int permsint = 0;
		
		for (int i=0; i<permsOrder.length; i++) {
			if (permsOrder[i]==null) continue;
			if (perms.contains(permsOrder[i])) {
				permsint|=1<<i;
			}
		}
		
		return permsint;
	}
	
	public static PermissionSet getNativePermissions(int permissions) {
		Set<Permission> perms = new HashSet<Permission>();
		
		for (int i=0; i<permsOrder.length; i++) {
			if (permsOrder[i]==null) continue;
			if (((permissions>>i)&1)==1) {
				perms.add(permsOrder[i]);
			}
		}
		
		return PermissionSet.of(perms.toArray(new Permission[perms.size()]));
	}
	
	private static Permission[] permsOrder = new Permission[] {
		Permission.SEND_MESSAGES, Permission.KICK_MEMBERS, Permission.BAN_MEMBERS, Permission.EMBED_LINKS,
		Permission.ADD_REACTIONS, Permission.MANAGE_EMOJIS, Permission.CONNECT, Permission.SPEAK,
		Permission.MANAGE_ROLES, Permission.MANAGE_MESSAGES, Permission.MANAGE_CHANNELS, Permission.MANAGE_GUILD,
		Permission.MANAGE_EMOJIS, Permission.USE_EXTERNAL_EMOJIS, Permission.PRIORITY_SPEAKER, Permission.MANAGE_ROLES,
		Permission.VIEW_CHANNEL
	};
}
