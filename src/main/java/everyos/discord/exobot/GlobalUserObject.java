package everyos.discord.exobot;

import discord4j.core.object.entity.User;
import everyos.discord.exobot.util.UserHelper;

public class GlobalUserObject {
	public User user;
	public String id;

	public GlobalUserObject(User user) {
		this.user = user;
		this.id = UserHelper.getUserId(user);
	}

	public String serializeSave() {
		return "{\"id\":\""+id+"\"}";
	}
}
