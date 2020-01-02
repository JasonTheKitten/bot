package everyos.discord.exobot;

import java.util.HashMap;

import discord4j.core.object.entity.Channel;
import discord4j.core.object.entity.Guild;
import everyos.discord.exobot.util.ChannelHelper;
import everyos.discord.exobot.util.GuildHelper;
import reactor.core.publisher.Mono;

public class GuildObject {
	public String id;
	public Guild guild;
	public HashMap<String, ChannelObject> channels;
	public HashMap<String, UserObject> users;
	public String prefix;
	
	public GuildObject(Guild guild) {
		this.guild = guild;
		this.id = GuildHelper.getGuildId(guild);
		this.channels = new HashMap<String, ChannelObject>();
		this.users = new HashMap<String, UserObject>();
		this.prefix = "*";
	}
	
	public ChannelObject getChannel(Mono<Channel> channel) {
		return ChannelHelper.getChannelData(this, channel);
	}
	public ChannelObject getChannel(Channel channel) {
		return ChannelHelper.getChannelData(this, channel);
	}
	public ChannelObject getChannel(String channel) {
		return ChannelHelper.getChannelData(this, channel);
	}

	public String serializeSave() {
		StringBuilder save = new StringBuilder("{\"id\":\""+id+"\",\"channels\":{");
		channels.forEach((k, v)->{
			save.append("\""+k+"\":"+v.serializeSave()+",");
		});
		save.append("},\"users\":{");
		users.forEach((k, v)->{
			save.append("\""+k+"\":"+v.serializeSave()+",");
		});
		save.append("},\"prefix\":\""+prefix+"\"}");
		return save.toString();
	}
}
