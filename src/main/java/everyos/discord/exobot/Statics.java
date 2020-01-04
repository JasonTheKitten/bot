package everyos.discord.exobot;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import discord4j.core.DiscordClient;
import everyos.discord.exobot.commands.ICommand;
import everyos.discord.exobot.objects.GlobalUserObject;
import everyos.discord.exobot.objects.GuildObject;
import everyos.discord.exobot.webserver.WebServer;

public class Statics {
	public static HashMap<String, ICommand> commands;
	public static HashMap<String, GuildObject> guilds;
	public static HashMap<String, GlobalUserObject> users;
	public static HashMap<String, String> twitchchannels;
	public static DiscordClient client;
	public static ArrayList<WebServer> servers;
	
	public static void loadSave() {}
	public static String serializeSave() {
		StringBuilder save = new StringBuilder("{\"guilds\":[");
		guilds.forEach((k, v)->{
			save.append(v.serializeSave()+",");
		});
		save.append("], \"users\":[");
		users.forEach((k, v)->{
			save.append(v.serializeSave()+",");
		});
		save.append("], \"twitchchannels\":{");
		twitchchannels.forEach((k, v)->{
			save.append("\""+k+"\":\""+v+"\",");
		});
		save.append("}}");
		return save.toString().replaceAll(",}", "}").replaceAll(",]", "]");
	}
	public static void loadSave(String argument) {
		try {
			JsonObject save = JsonParser.parseString(argument).getAsJsonObject();
			save.get("guilds").getAsJsonArray().forEach(v->{
				try {
					GuildObject curGuild = new GuildObject(v.getAsJsonObject());
					guilds.put(curGuild.id, curGuild);
				} catch (Exception e) {e.printStackTrace();}
			});
			save.get("users").getAsJsonArray().forEach(v->{
				try {
					GlobalUserObject curUser = new GlobalUserObject(v.getAsJsonObject());
					users.put(curUser.id, curUser);
				} catch (Exception e) {e.printStackTrace();}
			});
		} catch (Exception e) {e.printStackTrace();}
	}
}
