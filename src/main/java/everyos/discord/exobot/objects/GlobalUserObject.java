package everyos.discord.exobot.objects;

import com.google.gson.JsonObject;

import discord4j.core.object.entity.User;
import discord4j.core.object.util.Snowflake;
import everyos.discord.exobot.Statics;
import everyos.discord.exobot.util.UserHelper;
import everyos.discord.exobot.util.SaveUtil.JSONObject;

public class GlobalUserObject {
	public User user;
	public String id;

	public GlobalUserObject(User user) {
		this.user = user;
		this.id = UserHelper.getUserId(user);
	}

	public GlobalUserObject(JsonObject save) {
		this.id = save.get("id").getAsString();
		this.user = Statics.client.getUserById(Snowflake.of(this.id)).block();
	}

	public JSONObject serializeSave() {
        JSONObject save = new JSONObject();
        save.put("id", id);

        return save;
	}
}
