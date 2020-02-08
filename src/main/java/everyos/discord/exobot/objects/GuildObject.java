package everyos.discord.exobot.objects;

import java.util.HashMap;

import com.google.gson.JsonObject;

import discord4j.core.object.entity.Channel;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.util.Snowflake;
import everyos.discord.exobot.Statics;
import everyos.discord.exobot.util.ChannelHelper;
import everyos.discord.exobot.util.GuildHelper;
import everyos.discord.exobot.util.SaveUtil.JSONArray;
import everyos.discord.exobot.util.SaveUtil.JSONObject;
import reactor.core.publisher.Mono;

public class GuildObject {
	public String id;
	public Guild guild;
	public HashMap<String, ChannelObject> channels;
	public HashMap<String, UserObject> users;
    public String prefix;
    public int i;
    public int dailymoney;
    public int chatmoney;
    public boolean styledRolesEnabled;
	
	public GuildObject(Guild guild) {
		this.guild = guild;
		this.id = GuildHelper.getGuildId(guild);
		this.channels = new HashMap<String, ChannelObject>();
		this.users = new HashMap<String, UserObject>();
        this.prefix = "*";
        this.i = 0;
        this.dailymoney = 100;
        this.chatmoney = 1;
        this.styledRolesEnabled = false;
	}
	
	public GuildObject(JsonObject save) {
		this.channels = new HashMap<String, ChannelObject>();
		this.users = new HashMap<String, UserObject>();
		
		this.id = save.get("id").getAsString();
		this.guild = Statics.client.getGuildById(Snowflake.of(this.id)).block();
        this.prefix = save.get("prefix").getAsString();
        this.i = save.has("i")?save.get("i").getAsInt():0;
        this.dailymoney = save.has("dailymoney")?save.get("dailymoney").getAsInt():100;
        this.chatmoney = save.has("chatmoney")?save.get("chatmoney").getAsInt():1;
        this.styledRolesEnabled = save.has("enablestyledroles")?save.get("enablestyledroles").getAsBoolean():false;
		
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

	public JSONObject serializeSave() {
        JSONObject save = new JSONObject();
        save.put("id", id);
        save.put("prefix", prefix);
        save.put("i", i);
        save.put("dailymoney", dailymoney);
        save.put("chatmoney", chatmoney);

        final JSONArray array = new JSONArray();
        channels.forEach((k, v)->array.put(v.serializeSave()));
        save.put("channels", array);

        array.clear();
        users.forEach((k, v)->array.put(v.serializeSave()));
        save.put("users", array);

        return save;
	}
}
