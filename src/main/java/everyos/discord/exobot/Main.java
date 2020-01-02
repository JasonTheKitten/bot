package everyos.discord.exobot;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import discord4j.core.DiscordClient;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.event.EventDispatcher;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.data.stored.PresenceBean;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.Message.Type;
import discord4j.core.object.presence.Activity;
import discord4j.core.object.presence.Presence;
import everyos.discord.exobot.cases.ChannelCase;
import everyos.discord.exobot.commands.BanCommand;
import everyos.discord.exobot.commands.BotExcludeCommand;
import everyos.discord.exobot.commands.BotIncludeCommand;
import everyos.discord.exobot.commands.EchoCommand;
import everyos.discord.exobot.commands.FullHelpCommand;
import everyos.discord.exobot.commands.HelpCommand;
import everyos.discord.exobot.commands.ICommand;
import everyos.discord.exobot.commands.KickCommand;
import everyos.discord.exobot.commands.LogSaveCommand;
import everyos.discord.exobot.commands.OptCommand;
import everyos.discord.exobot.commands.PrefixCommand;
import everyos.discord.exobot.commands.PurgeCommand;
import everyos.discord.exobot.commands.RemindCommand;
import everyos.discord.exobot.commands.SuggestionConfigCommand;
import everyos.discord.exobot.commands.TwitchConfigCommand;
import everyos.discord.exobot.commands.UnbanCommand;
import everyos.discord.exobot.commands.UnoptCommand;
import everyos.discord.exobot.util.CommandHelper;
import everyos.discord.exobot.util.GuildHelper;
import everyos.discord.exobot.util.MessageHelper;
import everyos.discord.exobot.util.StringUtil;
import everyos.discord.exobot.webserver.WebServer;

public class Main {
	public static void main(String[] rargs) throws Exception {
		System.out.println("Command is running");
		
		String[] args;
		
		if (rargs.length<2) {
			args = new String[2];
			
			System.out.println("User Client ID and Bot Token both expected");
			Scanner s = new Scanner(System.in);
			System.out.println("Enter Client ID:");
			args[0] = s.next();
			System.out.println("Enter Bot Token:");
			args[1] = s.next();
			s.close();
		} else {args=rargs;}
		
		HashMap<String, ICommand> commands = new HashMap<String, ICommand>();
		Statics.commands = commands;
		Statics.guilds = new HashMap<String, GuildObject>();
		Statics.users = new HashMap<String, GlobalUserObject>();
		Statics.twitchchannels = new HashMap<String, String>();
		Statics.servers = new ArrayList<WebServer>();
		
		CommandHelper.register("help", new HelpCommand());
		CommandHelper.register("echo", new EchoCommand());
		CommandHelper.register("prefix", new PrefixCommand());
		CommandHelper.register("configsuggest", new SuggestionConfigCommand());
		CommandHelper.register("opt", new OptCommand());
		CommandHelper.register("unopt", new UnoptCommand());
		CommandHelper.register("botinclude", new BotIncludeCommand());
		CommandHelper.register("botexclude", new BotExcludeCommand());
		CommandHelper.register("twitchconfig", new TwitchConfigCommand());
		CommandHelper.register("kick", new KickCommand());
		CommandHelper.register("ban", new BanCommand());
		CommandHelper.register("unban", new UnbanCommand());
		CommandHelper.register("fullhelp", new FullHelpCommand());
		CommandHelper.register("purge", new PurgeCommand());
		CommandHelper.register("remind", new RemindCommand());
		CommandHelper.register("logsave", new LogSaveCommand());
		
		final DiscordClient client = new DiscordClientBuilder(args[1]).build();
		
		Statics.client = client;
		
		EventDispatcher dispatcher = client.getEventDispatcher();
		dispatcher.on(ReadyEvent.class)
        	.subscribe(ready -> {
        		System.out.println("Bot running at https://discordapp.com/oauth2/authorize?&client_id="+args[0]+"&scope=bot&permissions=8");
        		PresenceBean presence = new PresenceBean();
        		presence.setStatus("Spaghetti");
        		client.updatePresence(Presence.online(Activity.playing("dead"))).block();
        	});
		
		dispatcher.on(MessageCreateEvent.class)
	    	.subscribe(messageevent -> {
	    		Message message = messageevent.getMessage();
	    		try {
	    			ChannelCase.CASES special = ChannelCase.getSpecial(GuildHelper.getGuildData(message.getGuild()), message.getChannel().block());
	    			if (special!=ChannelCase.CASES.NULL) {
	    				ChannelCase.execute(special, message); return;
	    			}
		    		if (messageevent.getMember().get().isBot()) return;
		    		if (!(message.getType() == Type.DEFAULT)) {return;}
		    		String content = message.getContent().get();
		    		String prefix = GuildHelper.getGuildData(message.getGuild()).prefix;
		    		if (content.startsWith(prefix)) {
		    			String msg = StringUtil.sub(content, prefix.length());
		    			int space = msg.indexOf(" ");
		    			if (space<=0) space=content.length();
		    			if (space<=0) space=1;
		    			String cmd = StringUtil.sub(msg, 0, space);
		    			String argstr = StringUtil.sub(msg, space+1, content.length());
		    			if (cmd==""){cmd="help";}
		    			
		    			ICommand exe = commands.getOrDefault(cmd, commands.get("help"));
		    			exe.execute(message, argstr);
		    		}
	    		} catch(Exception e) {
	    			e.printStackTrace();
	    			try {
	    				MessageHelper.send(message.getChannel(), e.getClass().getSimpleName()+":"+e.getMessage(), true);
	    			} catch(Exception e2) {e2.printStackTrace();}
	    		}
	    	});
		
		Frame logout = new Frame("Close this window to logout Discord bot");
		logout.addWindowListener(new WindowListener(){
			@Override public void windowClosing(WindowEvent e) {
				logout.dispose();
				client.logout().block();
				Statics.servers.forEach(k->{
					k.close();
				});
			}
			@Override public void windowOpened(WindowEvent e) {}
			@Override public void windowClosed(WindowEvent e) {}
			@Override public void windowIconified(WindowEvent e) {}
			@Override public void windowDeiconified(WindowEvent e) {}
			@Override public void windowActivated(WindowEvent e) {}
			@Override public void windowDeactivated(WindowEvent e) {}
		});
		logout.setPreferredSize(new Dimension(400, 0));
		logout.setResizable(false);
		logout.pack();
		logout.setVisible(true);
		
		client.login().block();
	}
}
