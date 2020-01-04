package everyos.discord.exobot.commands;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import com.google.gson.JsonParser;

import discord4j.core.object.entity.Message;
import everyos.discord.exobot.StaticFunctions;
import everyos.discord.exobot.objects.ChannelObject;
import everyos.discord.exobot.util.ChannelHelper;
import everyos.discord.exobot.util.GuildHelper;
import everyos.discord.exobot.util.StreamHelper;
import everyos.discord.exobot.util.UserHelper;
import everyos.discord.exobot.webserver.WebServer;

public class TwitchConfigCommand implements ICommand {
	@Override public void execute(Message message, String argument) {
		ChannelObject channel = ChannelHelper.getChannelData(GuildHelper.getGuildData(message.getGuild()), message.getChannel().block());
		if (!UserHelper.getUserData(GuildHelper.getGuildData(message.getGuild()), message.getAuthorAsMember()).isOpted()) {
			channel.send("User is not opted to use this command", true);
			
			return;
		}
		
		try {
			WebServer.getDefaultServer();
			
			HttpsURLConnection req = (HttpsURLConnection) new URL("https://api.twitch.tv/helix/users?login=EveryBlock01").openConnection();
			
			req.setRequestMethod("GET");
			req.setRequestProperty("Accept-Content-Type", "application/json; charset=UTF-8");
			req.setRequestProperty("Client-ID", "l83ar9oxtcdcrm2uupt68znkjtgq6h");
			
			InputStream userstream;
			try {
				userstream = req.getInputStream();
			} catch (Exception e) {
				InputStream streamIn = req.getErrorStream();
				
				try {
					channel.send(StreamHelper.read(streamIn).toString(), false);
				} catch(Exception e2) {}
				
				streamIn.close();
				
				throw e;
			}
			String udata = StreamHelper.read(userstream);
			System.out.println(udata);
			String uid = JsonParser.parseString(udata).getAsJsonObject().get("data").getAsJsonArray().get(0).getAsJsonObject().get("id").getAsString();
			System.out.println(uid);
			
			/////
			
			String cb = "https://distwit.pagekite.me/twitch";
			String postBody =
				"{\n"+
				"	\"hub.callback\":\""+cb+"\",\n"+
				"	\"hub.mode\":\"subscribe\",\n"+
				"	\"hub.topic\":\"https://api.twitch.tv/helix/streams?user_id="+uid+"\",\n"+
				"	\"hub.lease_seconds\":600\n"+
				"}";
			
			req = (HttpsURLConnection) new URL("https://api.twitch.tv/helix/webhooks/hub").openConnection();
			
			req.setRequestMethod("POST");
			req.setRequestProperty("Accept-Content-Type", "application/json; charset=UTF-8");
			req.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
			req.setRequestProperty("Client-ID", "l83ar9oxtcdcrm2uupt68znkjtgq6h");
			req.setDoOutput(true);
			
			
			OutputStream streamOut = req.getOutputStream();
			streamOut.write(postBody.getBytes("UTF-8"));
			streamOut.close();
			
			try {
				req.getInputStream();
			} catch (Exception e) {
				InputStream streamIn = req.getErrorStream();
				
				try {
					channel.send(StreamHelper.read(streamIn), false);
				} catch(Exception e2) {}
				
				streamIn.close();
				
				throw e;
			}
			
			req.disconnect();
			
			StaticFunctions.save();
			channel.send("Success! (Still WIP though)", true);
		} catch (IOException e) {
			channel.send("Failed!", true);
			e.printStackTrace();
		}
	}
	@Override public String getHelp() {
		return " Unfinished WIP, will provide Twitch integration ";
	}
	@Override public COMMANDS getType() {
		return COMMANDS.Incomplete;
	}
	
	@Override public String getFullHelp() {
		return "Work in progress. WARNING: WILL OPEN A TCP SOCKET ON YOUR NETWORK.";
	}
}
