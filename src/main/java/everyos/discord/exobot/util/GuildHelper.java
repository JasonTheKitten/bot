package everyos.discord.exobot.util;

import discord4j.core.object.entity.Guild;
import everyos.discord.exobot.Statics;
import everyos.discord.exobot.objects.GuildObject;
import reactor.core.publisher.Mono;

public class GuildHelper {
	public static String getGuildID(Mono<Guild> guild) {
		return getGuildId(guild.block());
	}
	public static String getGuildId(Guild guild) {
		return guild.getId().asString();
	}
	
	public static GuildObject getGuildData(Mono<Guild> guild) {
		return getGuildData(guild.block());
	}
	public static GuildObject getGuildData(Guild guild) {
		GuildObject data = Statics.guilds.get(guild.getId().asString());
		if (data==null) {
			data = new GuildObject(guild);
			Statics.guilds.put(guild.getId().asString(), data);
		}
		return data;
    }
    public static GuildObject getGuildData(String gid) {
		GuildObject data = Statics.guilds.get(gid);
		if (data==null) {
			data = new GuildObject(gid);
			Statics.guilds.put(gid, data);
		}
		return data;
	}
}
