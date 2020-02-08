package everyos.discord.exobot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import discord4j.core.DiscordClient;
import everyos.discord.exobot.commands.ICommand;
import everyos.discord.exobot.objects.GlobalUserObject;
import everyos.discord.exobot.objects.GuildObject;
import everyos.discord.exobot.util.SaveUtil.JSONArray;
import everyos.discord.exobot.util.SaveUtil.JSONObject;
import everyos.discord.exobot.webserver.WebServer;

public class Statics {
    public static DiscordClient client;
	public static HashMap<String, ICommand> commands;
	public static HashMap<String, GuildObject> guilds;
	public static HashMap<String, GlobalUserObject> users;
	public static HashMap<String, String> twitchchannels;
    public static ArrayList<WebServer> servers;

    public final static Timer timer = new Timer();
	
	public static void loadSave() {}
	public static String serializeSave() {
        JSONObject save = new JSONObject();

        final JSONArray array = new JSONArray();
        guilds.forEach((k, v)->array.put(v.serializeSave()));
        save.put("guilds", array);

        array.clear();
        users.forEach((k, v)->array.put(v.serializeSave()));
        save.put("users", array);

        array.clear();
        guilds.forEach((k, v)->array.put(v.serializeSave()));
        save.put("twitchchannels", array);

		return save.toString();
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
