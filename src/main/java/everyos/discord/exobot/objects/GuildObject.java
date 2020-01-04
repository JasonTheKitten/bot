package everyos.discord.exobot.objects;

import java.util.HashMap;

import com.google.gson.JsonObject;

import discord4j.core.object.entity.Channel;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.util.Snowflake;
import everyos.discord.exobot.Statics;
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
	
	public GuildObject(JsonObject save) {
		this.channels = new HashMap<String, ChannelObject>();
		this.users = new HashMap<String, UserObject>();
		
		this.id = save.get("id").getAsString();
		this.guild = Statics.client.getGuildById(Snowflake.of(this.id)).block();
		this.prefix = save.get("prefix").getAsString();
		
		save.get("channels").getAsJsonArray().forEach(v->{
			try {
				ChannelObject curChannel = new ChannelObject(this, v.getAsJsonObject());
				channels.put(curChannel.id, curChannel);
			} catch (Exception e) {e.printStackTrace();}
		});
		save.get("users").getAsJsonArray().forEach(v->{
			try {
				UserObject curUser = new UserObject(this, v.getAsJsonObject());
				users.put(curUser.id, curUser);
			} catch (Exception e) {e.printStackTrace();}
		});
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
		StringBuilder save = new StringBuilder("{\"id\":\""+id+"\",\"channels\":[");
		channels.forEach((k, v)->{
			save.append(v.serializeSave()+",");
		});
		save.append("],\"users\":[");
		users.forEach((k, v)->{
			save.append(v.serializeSave()+",");
		});
		save.append("],\"prefix\":\""+prefix+"\"}");
		return save.toString();
	}
}
