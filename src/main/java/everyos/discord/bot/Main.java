package everyos.discord.bot;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import discord4j.core.DiscordClient;
import discord4j.core.event.EventDispatcher;
import discord4j.core.event.domain.guild.GuildCreateEvent;
import discord4j.core.event.domain.guild.GuildDeleteEvent;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.lifecycle.ReconnectEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Channel;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.object.presence.Activity;
import discord4j.core.object.presence.Presence;
import discord4j.core.shard.ShardingClientBuilder;
import everyos.discord.bot.adapter.ChannelAdapter;
import everyos.discord.bot.adapter.MessageAdapter;
import everyos.discord.bot.channelcase.ChatLinkChannelCase;
import everyos.discord.bot.channelcase.DefaultChannelCase;
import everyos.discord.bot.channelcase.IChannelCase;
import everyos.discord.bot.channelcase.SuggestionChannelCase;
import everyos.discord.bot.localization.LocalizedString;
import everyos.discord.bot.parser.ArgumentParser;
import everyos.discord.bot.standards.GuildDocumentCreateStandard;
import everyos.discord.bot.standards.MemberDocumentCreateStandard;
import everyos.discord.bot.util.FileUtil;
import everyos.discord.bot.util.ObjectStore;
import everyos.storage.database.DBDocument;
import everyos.storage.database.Database;

public class Main {
	public static DiscordClient client;
	public static Database db;
	public static long uptime = System.currentTimeMillis();
    public static AtomicLong cuptime = new AtomicLong();
    public static String clientID;
    public static void main(String[] args) {
    	//TODO: Cut back on nesting some
        System.out.println("Command is running");
        String clientSecret;
        
        AtomicInteger servercount = new AtomicInteger();
        AtomicBoolean shutdown = new AtomicBoolean();
        File keys = new File(FileUtil.getAppData(Constants.keysFile));
        if (args.length >= 2) {
            clientID = args[0];
            clientSecret = args[1];
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
            	e.printStackTrace();
                System.out.println("Credentials prompt failed");
                return;
            }
        }

        if (!keys.exists()) {
            try {
                keys.getParentFile().mkdirs();
                BufferedWriter writer = new BufferedWriter(new FileWriter(keys.getAbsolutePath()));
                writer.write(args[0] + " " + args[1]);
                writer.close();
            } catch (IOException e) {
                System.out.println("Could not save keys!");
            }
        }

        Database db = new Database(FileUtil.getAppData("database"));
        Main.db = db;
        
        HashMap<String, IChannelCase> cases = new HashMap<String, IChannelCase>();
        cases.put("default", new DefaultChannelCase()); //TODO: Convert these to enums representing ints
        cases.put("chatlink", new ChatLinkChannelCase());
        cases.put("suggestion", new SuggestionChannelCase());
        
        new Thread(()->{
    		int oldRecount = 0;
        	while (!shutdown.get()) {
        		try {
        			Thread.sleep(5000); //TODO: Use System.currentTimeMillis instead
        		} catch (Exception e) {e.printStackTrace();}
        		if (oldRecount!=servercount.get()) {
        			oldRecount = servercount.get();
        			onRecount(client, oldRecount);
        		}
        	}
        }).start();

        new ShardingClientBuilder(clientSecret).build()
        	.map(clientb->clientb.build())
        	.map(client->{
		        Main.client = client; //Will be run multiple times
		        
		        EventDispatcher dispatcher = client.getEventDispatcher();
		        dispatcher.on(ReadyEvent.class).subscribe(ready -> {
					System.out.println("Bot running at https://discordapp.com/oauth2/authorize?&client_id=" +
						clientID + "&scope=bot&permissions=1345842518");
				    servercount.set(0);
				    onWakeup(client);
				    cuptime.set(System.currentTimeMillis()); //TODO: Shards bork this
				});
		        dispatcher.on(ReconnectEvent.class).subscribe(e -> {
		        	cuptime.set(System.currentTimeMillis());
		        });
		        dispatcher.on(GuildCreateEvent.class).subscribe(e -> {
		        	servercount.incrementAndGet();
		        });
		        dispatcher.on(GuildDeleteEvent.class).subscribe(e -> {
		        	servercount.decrementAndGet();
		        });
		
		        dispatcher.on(MessageCreateEvent.class).subscribe(messageevent->{
		        	try {
		                Message message = messageevent.getMessage();
		                if (message.getType()!=Message.Type.DEFAULT) return;
		                
		                User invoker = message.getAuthor().orElse(null);
		                if (invoker==null||invoker.isBot()) return;
			            
			            MessageAdapter madapter = MessageAdapter.of(message);
		                
			            message.getChannel().subscribe(channel->{
			            	ObjectStore mode = new ObjectStore("default");
			            	if (channel.getType()==Channel.Type.GUILD_TEXT) {
					            messageevent.getGuildId().ifPresent(snowflake->{
					                DBDocument guild = db.collection("guilds").getOrSet(snowflake.asString(), GuildDocumentCreateStandard.standard);
					            	db.collection("channels").getIfPresent(message.getChannelId().asString(), channelo->{
					            		mode.object = channelo.getObject().getOrDefaultString("type", (String) mode.object);
					            	});
					            	message.getAuthor().ifPresent(user->{
					            		DBDocument usero = guild.subcollection("members").getOrSet(user.getId().asString(), MemberDocumentCreateStandard.standard);
					            		usero.getObject().set("feth", usero.getObject().getOrDefaultInt("feth", 0)+3);
					            		usero.save();
					            	});
					            });
			            	} else if (channel.getType()==Channel.Type.DM||channel.getType()==Channel.Type.GROUP_DM) {
			            		ChannelAdapter.of(channel);
			            	} else return;
		                    
		                    if (ArgumentParser.isDirectPing(message.getContent().orElse(""))) {
		                        madapter.getChannelAdapter(cadapter->{
		                            madapter.formatTextLocale(LocalizedString.OnPing, text->cadapter.send(text));
		                        });
		                        return;
		                    }
		
		                    IChannelCase chcase = cases.get(mode.object);
		                    if (chcase == null) return;
			            	chcase.execute(message, madapter);
			            });
		        	} catch (Exception e) {
		        		e.printStackTrace();
		        	}
		        });
		
		        Runtime.getRuntime().addShutdownHook(new Thread(()->{
		        	client.logout().subscribe();
		        	shutdown.set(true);
		        }));
		        
		        return client;
	        })
        	.flatMap(DiscordClient::login).blockLast(); //If all of this actually works, I'm stumped
    }
    
    private static void onWakeup(DiscordClient client) {
    	client.updatePresence(Presence.online(Activity.playing("sleepyhead"))).subscribe();
    }
    private static void onRecount(DiscordClient client, int c) {
    	client.updatePresence(Presence.online(Activity.watching(c+" server"+(c!=1?"s":"")+" | --- help | ---!"))).subscribe();
    }
}