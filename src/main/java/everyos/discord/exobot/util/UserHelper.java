package everyos.discord.exobot.util;

import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.User;
import discord4j.core.object.util.Snowflake;
import everyos.discord.exobot.objects.GuildObject;
import everyos.discord.exobot.objects.UserObject;
import reactor.core.publisher.Mono;

public class UserHelper {
	public static String getUserId(Mono<User> user) {
		return getUserId(user.block());
	}
	public static String getUserId(User user) {
		return user.getId().asString();
	}
	
	public static UserObject getUserData(GuildObject guild, Mono<Member> mono) {
		return getUserData(guild, mono.block());
	}
	public static UserObject getUserData(GuildObject guild, String user) {
        String ruser = user;
        if (isUserId(ruser)) ruser = parseUserId(ruser);
		return getUserData(guild, guild.guild.getMemberById(Snowflake.of(ruser)).block());
	}
	public static UserObject getUserData(GuildObject guild, Member user) {
		UserObject data = guild.users.get(user.getId().asString());
		if (data==null) {
			data = new UserObject(guild, user);
			guild.users.put(user.getId().asString(), data);
		}
		return data;
	}
	
	public static String parseUserId(String arg) {
		if (!isUserId(arg)) return null;
		return arg.substring(2, arg.length()-1);
	}
	public static boolean isUserId(String arg) {
		if (arg==null) return false;
		return arg.startsWith("<@") && arg.endsWith(">");
	}
}
