package everyos.discord.exobot.util;

import discord4j.core.object.entity.Channel;
import everyos.discord.exobot.objects.ChannelObject;
import everyos.discord.exobot.objects.GuildObject;
import reactor.core.publisher.Mono;

public class ChannelHelper {
	public static String getChannelID(Mono<Channel> channel) {
		return getChannelId(channel.block());
	}
	public static String getChannelId(Channel channel) {
		return channel.getId().asString();
	}
	
	public static ChannelObject getChannelData(GuildObject guild, Mono<Channel> channel) {
		return getChannelData(guild, channel.block());
	}
	public static ChannelObject getChannelData(GuildObject guild, String channel) {
		String rchannel = channel;
		if (isChannelId(rchannel)) rchannel = parseChannelId(rchannel);
		ChannelObject data = guild.channels.get(channel);
		if (data==null) {
			data = new ChannelObject(guild, channel);
			guild.channels.put(channel, data);
		}
		return data;
	}
	public static ChannelObject getChannelData(GuildObject guild, Channel channel) {
		ChannelObject data = guild.channels.get(channel.getId().asString());
		if (data==null) {
			data = new ChannelObject(guild, channel);
			guild.channels.put(channel.getId().asString(), data);
		}
		return data;
	}
	
	public static String parseChannelId(String arg) {
		if (!isChannelId(arg)) return null;
		return arg.substring(2, arg.length()-1);
	}
	public static boolean isChannelId(String arg) {
		return arg.startsWith("<#") && arg.endsWith(">");
	}
}
