package everyos.discord.bot;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import discord4j.core.DiscordClientBuilder;
import discord4j.core.event.EventDispatcher;
import discord4j.core.event.domain.channel.TypingStartEvent;
import discord4j.core.event.domain.guild.GuildCreateEvent;
import discord4j.core.event.domain.guild.GuildDeleteEvent;
import discord4j.core.event.domain.guild.MemberJoinEvent;
import discord4j.core.event.domain.guild.MemberLeaveEvent;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.lifecycle.ReconnectEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.event.domain.message.ReactionAddEvent;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.rest.util.Snowflake;
import everyos.discord.bot.adapter.ChannelAdapter;
import everyos.discord.bot.adapter.ChatLinkAdapter;
import everyos.discord.bot.adapter.GuildAdapter;
import everyos.discord.bot.event.MessageCreateEventHandler;
import everyos.discord.bot.event.ReactionAddEventHandler;
import everyos.storage.database.DBArray;
import everyos.storage.database.Database;
import everyos.storage.database.FileUtil;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

public class BotInstance {
    public static void main(String args[]) {
        System.out.println("Command is running");

        String clientID; String clientSecret;
        String giphyKey = null; String rapidKey = null;
        String yandexKey = null; String moderationKey = null;
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
                	if (s.hasNext()) {
	                	System.out.println("Enter Moderation Key:");
	                	moderationKey = s.next();
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
        
        new BotInstance(Long.valueOf(clientID), clientSecret, giphyKey, rapidKey, yandexKey, moderationKey).start();
    }

    public long clientID;
    private String clientSecret;
    
    public String giphyKey;
    public String rapidKey;
    public String yandexKey;

    public Database db;
    public long uptime;
	public AtomicBoolean shutdown;
	public AtomicInteger serverCount;
	public String moderationKey;

    public BotInstance(long id, String secret, String gk, String rk, String yk, String mk) {
        clientID = id;
        clientSecret = secret;
        
        giphyKey = gk;
        rapidKey = rk;
        yandexKey = yk;
        moderationKey = mk;

        db = Database.of(FileUtil.getAppData("database"));
        uptime = System.currentTimeMillis();
        
        shutdown = new AtomicBoolean();
        serverCount = new AtomicInteger();
    }

    public void start() {
    	DiscordClientBuilder.create(clientSecret)
    		.build()
            .withGateway(client->{
                ShardInstance shard = new ShardInstance(this, client);
                
                AtomicInteger shardGuildCount = new AtomicInteger();
                
                MessageCreateEventHandler mcehandler = new MessageCreateEventHandler(shard);
		        ReactionAddEventHandler raeh = new ReactionAddEventHandler(shard);

                EventDispatcher dispatcher = client.getEventDispatcher();

                Flux<?> m1 = dispatcher.on(ReadyEvent.class).doOnNext(e->{
                    System.out.println("Bot running at xxx");
                    shard.uptime = System.currentTimeMillis();
                    
                    serverCount.set(serverCount.get()-shardGuildCount.get());
                    shardGuildCount.set(0);
                    
                    shard.onWakeup();
                });
                Flux<?> m2 = dispatcher.on(ReconnectEvent.class).doOnNext(e->{
                    shard.uptime = System.currentTimeMillis();
                });
                
                Flux<?> m3 = dispatcher.on(GuildCreateEvent.class).doOnNext(e -> {
		        	serverCount.incrementAndGet();
		        	shardGuildCount.incrementAndGet();
		        });
                Flux<?> m4 = dispatcher.on(GuildDeleteEvent.class).doOnNext(e -> {
		        	serverCount.decrementAndGet();
		        	shardGuildCount.decrementAndGet();
		        });

                Flux<?> m5 = dispatcher.on(MessageCreateEvent.class)
                	.publishOn(Schedulers.boundedElastic())
                	.flatMap(e->mcehandler.handle(e));
                
                Flux<?> m6 = dispatcher.on(MemberJoinEvent.class)
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
	        		});
                
                Flux<?> m7 = dispatcher.on(MemberLeaveEvent.class)
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
	        		;
                
                Flux<?> m8 = dispatcher.on(ReactionAddEvent.class)
	    	        .flatMap(e->raeh.handle(e));
                
                Flux<?> m9 = dispatcher.on(TypingStartEvent.class)
                	.flatMap(e->{
                		return e.getUser()
                			.filter(u->!u.isBot())
                			.flatMap(u->ChannelAdapter.of(shard, e.getChannelId().asLong()).getData(obj->{
	                            if (obj.has("data")&&obj.getOrDefaultObject("data", null).has("chatlinkid"))  {
	                                return Mono.just(obj.getOrDefaultObject("data", null).getOrDefaultLong("chatlinkid", -1L));
	                            }
	                            return Mono.empty();
	                        })).cast(Long.class)
	                        .flatMapMany(s->{
	                        	ChatLinkAdapter adapter = ChatLinkAdapter.of(shard, s);
	                        	return adapter.onEachChannel(e.getChannelId().asLong(), (channel, id)->{
	                        		return channel.type();
	                        	});
	                        });
                	})
                	.onErrorResume(e->{e.printStackTrace(); return Mono.empty();});
                	
                return Mono.when(r(m1), r(m2), r(m3), r(m4), r(m5), r(m6), r(m7), r(m8), r(m9)).onErrorResume(e->{
                	e.printStackTrace();
                	return Mono.empty();
                });
            }).block();
    }
    
    public Flux<?> r(Flux<?> flux) {
    	return flux.onErrorResume(e->{
    		e.printStackTrace();
    		return Flux.empty();
    	});
    }
}