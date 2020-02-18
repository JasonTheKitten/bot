package everyos.discord.bot;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;

import discord4j.core.DiscordClient;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.event.EventDispatcher;
import discord4j.core.event.domain.guild.GuildCreateEvent;
import discord4j.core.event.domain.guild.GuildDeleteEvent;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Channel;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.presence.Activity;
import discord4j.core.object.presence.Presence;
import everyos.discord.bot.adapter.ChannelAdapter;
import everyos.discord.bot.channelcase.DefaultChannelCase;
import everyos.discord.bot.channelcase.IChannelCase;
import everyos.discord.bot.util.FileUtil;
import everyos.discord.bot.util.ObjectStore;
import everyos.storage.database.DBDocument;
import everyos.storage.database.Database;

public class Main {
	public static DiscordClient client;
	public static Database db;
    public static void main(String[] args) {
        System.out.println("Command is running");

        String clientID;
        String clientSecret;
        File keys = new File(FileUtil.getAppData(Constants.keysFile));
        if (args.length >= 2) {
            clientID = args[0];
            clientSecret = args[0];
        } else {
            args = new String[2];

            System.out.println("User Client ID and Bot Token both expected");
            try {
                Scanner s;
                s = keys.exists() ? new Scanner(keys) : new Scanner(System.in);
                System.out.println("Enter Client ID:");
                clientID = s.next();
                System.out.println("Enter Bot Token:");
                clientSecret = s.next();
                s.close();
            } catch (Exception e) {
                System.out.println("Credentials prompt failed");
                return;
            }
        }

        if (!keys.exists()) {
            try {
                keys.getParentFile().mkdirs();
                BufferedWriter writer = new BufferedWriter(new FileWriter(FileUtil.getAppData(Constants.keysFile)));
                writer.write(args[0] + " " + args[1]);
                writer.close();
            } catch (IOException e) {
                System.out.println("Could not save keys!");
            }
        }

        Database db = new Database(FileUtil.getAppData("database"));
        Main.db = db;
        
        HashMap<String, IChannelCase> cases = new HashMap<String, IChannelCase>();
        cases.put("default", new DefaultChannelCase());


        final DiscordClient client = new DiscordClientBuilder(clientSecret).build();
        Main.client = client;
        
        EventDispatcher dispatcher = client.getEventDispatcher();
        dispatcher.on(ReadyEvent.class).subscribe(ready -> {
            System.out.println("Bot running at https://discordapp.com/oauth2/authorize?&client_id=" + clientID  + "&scope=bot&permissions=8");
            onRecount();
        });
        dispatcher.on(GuildCreateEvent.class).subscribe(e -> onRecount());
        dispatcher.on(GuildDeleteEvent.class).subscribe(e -> onRecount());

        dispatcher.on(MessageCreateEvent.class).subscribe(messageevent->{
        	try {
	            Member invoker = messageevent.getMember().get();
	            if (invoker==null||invoker.isBot()) return;
	            
	            Message message = messageevent.getMessage();
	            if (message.getType()!=Message.Type.DEFAULT) return;
	            
	            message.getChannel().subscribe(channel->{
	            	ObjectStore mode = new ObjectStore("default");
		            ObjectStore adapter = new ObjectStore();
	            	if (channel.getType()==Channel.Type.GUILD_TEXT) {
			            messageevent.getGuildId().ifPresent(snowflake->{
			                DBDocument guild = db.collection("guilds").getOrSet(snowflake.asString(), dbdoc->{});
			            	db.collection("channels").getIfPresent(message.getChannelId().asString(), channelo->{
			            		mode.object = channelo.getObject().getOrDefaultString("type", (String) mode.object);
			            	});
			            	message.getAuthor().ifPresent(user->{
			            		DBDocument usero = guild.subcollection("users").getOrSet(user.getId().asString(), dbdoc->{});
			            		usero.getObject().set("feth", usero.getObject().getOrDefaultInt("feth", 0)+1);
			            		usero.save();
			            	});
			            	
			            	adapter.object = new ChannelAdapter(Channel.Type.GUILD_TEXT, channel, snowflake);
			            });
			            if (adapter.object==null) return;
	            	} else if (channel.getType()==Channel.Type.DM) {
	            		adapter.object = new ChannelAdapter(Channel.Type.DM, channel);
	            	} else if (channel.getType()==Channel.Type.GROUP_DM) {
	            		adapter.object = new ChannelAdapter(Channel.Type.GROUP_DM, channel);
	            	} else return;
	            	
	            	if (((ChannelAdapter) adapter.object).shouldIgnoreUser()) return;
		            
	            	IChannelCase chcase = cases.get(mode.object);
	            	if (chcase == null) return;
	            	chcase.execute(message, (ChannelAdapter) adapter.object);
	            });
        	} catch (Exception e) {
        		e.printStackTrace();
        	}
        });

        Runtime.getRuntime().addShutdownHook(new Thread(()->client.logout().subscribe()));
        
        client.login().block();
    }
    
    private static void onRecount() {
    	client.getGuilds().count().subscribe(c->{
        	client.updatePresence(Presence.online(Activity.watching("spiders make webs ("+c+" server"+(c!=1?"s":"")+")"))).subscribe();
        });
    }
}