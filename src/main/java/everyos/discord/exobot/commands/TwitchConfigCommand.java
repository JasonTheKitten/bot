package everyos.discord.exobot.commands;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import discord4j.core.object.entity.Message;
import everyos.discord.exobot.ChannelObject;
import everyos.discord.exobot.util.ChannelHelper;
import everyos.discord.exobot.util.GuildHelper;
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
			
			String cb = "https://distwit.pagekite.me/twitch";
			String postBody =
				"{\n"+
				"	\"hub.callback\":\""+cb+"\",\n"+
				"	\"hub.mode\":\"subscribe\",\n"+
				"	\"hub.topic\":\"https://api.twitch.tv/helix/streams?user_id="+"123"+"\",\n"+
				"	\"hub.lease_seconds\":600\n"+
				"}";
			
			HttpsURLConnection req = (HttpsURLConnection) new URL("https://api.twitch.tv/helix/webhooks/hub").openConnection();
			
			req.setRequestMethod("POST");
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
				StringBuilder reason = new StringBuilder();
				
				int ch;
				while ((ch=streamIn.read())!=-1) {
					reason.append((char) ch);
				}
				streamIn.close();
				
				try {
					channel.send(reason.toString(), false);
				} catch(Exception e2) {}
				
				throw e;
			}
			
			req.disconnect();
			
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
