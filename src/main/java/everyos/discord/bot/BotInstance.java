package everyos.discord.bot;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import discord4j.core.DiscordClient;
import discord4j.core.event.EventDispatcher;
import discord4j.core.event.domain.guild.GuildCreateEvent;
import discord4j.core.event.domain.guild.GuildDeleteEvent;
import discord4j.core.event.domain.guild.MemberJoinEvent;
import discord4j.core.event.domain.guild.MemberLeaveEvent;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.lifecycle.ReconnectEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.event.domain.message.ReactionAddEvent;
import discord4j.core.object.entity.MessageChannel;
import discord4j.core.object.util.Snowflake;
import discord4j.core.shard.ShardingClientBuilder;
import everyos.discord.bot.adapter.GuildAdapter;
import everyos.discord.bot.event.MessageCreateEventHandler;
import everyos.discord.bot.event.ReactionAddEventHandler;
import everyos.storage.database.DBArray;
import everyos.storage.database.Database;
import everyos.storage.database.FileUtil;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class BotInstance {
    public static void main(String args[]) {
        System.out.println("Command is running");

        String clientID; String clientSecret;
        String giphyKey = null; String rapidKey = null; String yandexKey = null;
        File keys = new File(FileUtil.getAppData("keys.config"));
        if (args.length >= 2) {
            clientID = args[0];
            clientSecret = args[1];
        } else {
            System.out.println("User Client ID and Bot Token both expected");
            try {
                Scanner 
                s = keys.exists() ? new Scanner(keys) : new Scanner(System.in);
                System.out.println("Enter Client ID:");
                clientID = s.next();
                System.out.println("Enter Bot Token:");
                clientSecret = s.next();
                
                if (keys.exists()) {
                	if (s.hasNext()) {
	                	System.out.println("Enter Giphy Key:");
	                	giphyKey = s.next();
                	}
                	if (s.hasNext()) {
	                	System.out.println("Enter Rapid Key:");
	                	rapidKey = s.next();
                	}
                	if (s.hasNext()) {
	                	System.out.println("Enter Yandex Key:");
	                	yandexKey = s.next();
                	}
                }
                
                s.close();
            } catch (Exception e) {
                System.out.println("Credentials prompt failed");
                return;
            }
        }

        if (!keys.exists())
            try {
                keys.getParentFile().mkdirs();
                BufferedWriter writer = new BufferedWriter(new FileWriter(keys.getAbsolutePath()));
                writer.write(clientID + " " + clientSecret);
                writer.close();
            } catch (IOException e) {
                System.out.println("Could not save keys!");
            }
        
        new BotInstance(clientID, clientSecret, giphyKey, rapidKey, yandexKey).start();
    }

    public String clientID;
    private String clientSecret;
    
    public String giphyKey;
    public String rapidKey;
    public String yandexKey;

    public Database db;
    public long uptime;
	public AtomicBoolean shutdown;
	public AtomicInteger serverCount;

    public BotInstance(String id, String secret, String gk, String rk, String yk) {
        clientID = id;
        clientSecret = secret;
        
        giphyKey = gk;
        rapidKey = rk;
        yandexKey = yk;

        db = Database.of(FileUtil.getAppData("database"));
        uptime = System.currentTimeMillis();
        
        shutdown = new AtomicBoolean();
        serverCount = new AtomicInteger();
    }

    public void start() {
        new ShardingClientBuilder(clientSecret).build()
            .map(client->client.build())
            .doOnNext(client->{
                ShardInstance shard = new ShardInstance(this, client);
                
                AtomicInteger shardGuildCount = new AtomicInteger();

                EventDispatcher dispatcher = client.getEventDispatcher();

                dispatcher.on(ReadyEvent.class).doOnNext(e->{
                    System.out.println("Bot running at xxx");
                    shard.uptime = System.currentTimeMillis();
                    
                    serverCount.set(serverCount.get()-shardGuildCount.get());
                    shardGuildCount.set(0);
                    
                    shard.onWakeup();
                }).subscribe();
                dispatcher.on(ReconnectEvent.class).doOnNext(e->{
                    shard.uptime = System.currentTimeMillis();
                }).subscribe();
                
                dispatcher.on(GuildCreateEvent.class).subscribe(e -> {
		        	serverCount.incrementAndGet();
		        	shardGuildCount.incrementAndGet();
		        });
		        dispatcher.on(GuildDeleteEvent.class).subscribe(e -> {
		        	serverCount.decrementAndGet();
		        	shardGuildCount.decrementAndGet();
		        });

		        MessageCreateEventHandler mcehandler = new MessageCreateEventHandler(shard);
                dispatcher.on(MessageCreateEvent.class)
                    .flatMap(e->mcehandler.handle(e))
                    .subscribe();
                
                dispatcher.on(MemberJoinEvent.class)
	        		.flatMap(e->{
	        			return e.getGuild().flatMap(guild->{
	        				ArrayList<String> roles = new ArrayList<String>();
	        				GuildAdapter gadapter = GuildAdapter.of(shard, guild);
	        				String welcomeMessage = gadapter.getData(obj->obj.getOrDefaultString("wmsg", null));
	        				String welcomeMessageChannel = gadapter.getData(obj->obj.getOrDefaultString("wmsgc", null));
	        				
	        				gadapter.getData(obj->{
	        					DBArray rolesa = obj.getOrDefaultArray("aroles", null);
	        					if (rolesa!=null) rolesa.forEach(i->{
	        						roles.add(rolesa.getString(i));
	        					});
	        					
	        					return null;
	        				});
	        				
	        				Mono<Void> mono = Mono.empty();
	        				if (welcomeMessage!=null&&welcomeMessageChannel!=null) {
	        					mono=mono.and(
	        						guild.getChannelById(Snowflake.of(welcomeMessageChannel)).cast(MessageChannel.class)
	        						.flatMap(m->m.createMessage(welcomeMessage)));
	        				}
	        				for (String role: roles) mono=mono.and(e.getMember().addRole(Snowflake.of(role)));
	        				
	        				return mono;
	        			});
	        		})
	        		.onErrorResume(e->{e.printStackTrace(); return Flux.empty();})
	        		.subscribe();
                
                dispatcher.on(MemberLeaveEvent.class)
	        		.flatMap(e->{
	        			return e.getGuild().flatMap(guild->{
	        				GuildAdapter gadapter = GuildAdapter.of(shard, guild);
	        				String leaveMessage = gadapter.getData(obj->obj.getOrDefaultString("lmsg", null));
	        				String leaveMessageChannel = gadapter.getData(obj->(obj.getOrDefaultString("lmsgc", null)));
	        				
	        				Mono<Void> mono = Mono.empty();
	        				if (leaveMessage!=null&&leaveMessageChannel!=null) {
	        					mono=mono.and(
	        						guild.getChannelById(Snowflake.of(leaveMessageChannel)).cast(MessageChannel.class)
	        						.flatMap(m->m.createMessage(leaveMessage)));
	        				}
	        				
	        				return mono;
	        			});
	        		})
	        		.onErrorResume(e->{e.printStackTrace(); return Flux.empty();})
	        		.subscribe();
                
                ReactionAddEventHandler raeh = new ReactionAddEventHandler(shard);
                dispatcher.on(ReactionAddEvent.class)
	    	        .flatMap(e->raeh.handle(e))
	    			.subscribe();
                	
            })
            .flatMap(DiscordClient::login).blockLast();
    }
}