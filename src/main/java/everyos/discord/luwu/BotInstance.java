package everyos.discord.luwu;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.reactivestreams.Publisher;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import discord4j.common.util.Snowflake;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.EventDispatcher;
import discord4j.core.event.domain.channel.TypingStartEvent;
import discord4j.core.event.domain.guild.GuildCreateEvent;
import discord4j.core.event.domain.guild.GuildDeleteEvent;
import discord4j.core.event.domain.guild.MemberJoinEvent;
import discord4j.core.event.domain.guild.MemberLeaveEvent;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.lifecycle.ReconnectEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.event.domain.message.MessageDeleteEvent;
import discord4j.core.event.domain.message.MessageUpdateEvent;
import discord4j.core.event.domain.message.ReactionAddEvent;
import discord4j.core.event.domain.message.ReactionRemoveEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.GuildMessageChannel;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.object.presence.Activity;
import discord4j.core.object.presence.Presence;
import everyos.discord.luwu.adapter.ChannelAdapter;
import everyos.discord.luwu.adapter.ChatLinkAdapter;
import everyos.discord.luwu.adapter.GuildAdapter;
import everyos.discord.luwu.database.DBArray;
import everyos.discord.luwu.database.DBObject;
import everyos.discord.luwu.database.Database;
import everyos.discord.luwu.event.MessageCreateEventHandler;
import everyos.discord.luwu.event.ReactionAddEventHandler;
import everyos.discord.luwu.event.ReactionRemoveEventHandler;
import everyos.discord.luwu.util.FileUtil;
import everyos.discord.luwu.util.UnirestUtil;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

public class BotInstance {
    public static void main(String args[]) {
        System.out.println("Command is running");

        String clientID; String clientSecret;
        String giphyKey = null; String rapidKey = null;
        String yandexKey = null; String moderationKey = null;
        String mongoKey = null;
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
                	if (s.hasNext()) {
	                	System.out.println("Enter MongoDB Key:");
	                	mongoKey = s.next();
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
        
        new BotInstance(Long.valueOf(clientID), clientSecret, giphyKey, rapidKey, yandexKey, moderationKey, mongoKey).start();
    }

    public long clientID;
    private String clientSecret;
    
    public String giphyKey;
    public String rapidKey;
    public String yandexKey;
    public String moderationKey;
	public String mongoKey;

    public Database db;
    public long uptime;
	public AtomicBoolean shutdown;
	public AtomicInteger serverCount;
	public GatewayDiscordClient client;
	public BotData data;
	public JsonObject config;

    public BotInstance(long id, String secret, String gk, String rk, String yk, String mk, String mk2) {
        clientID = id;
        clientSecret = secret;
        
        giphyKey = gk;
        rapidKey = rk;
        yandexKey = yk;
        moderationKey = mk;
        mongoKey = mk2;
        
        Hooks.onOperatorDebug();
        
        File configf = new File(FileUtil.getAppData("config.json"));
        InputStream in = ClassLoader.getSystemResourceAsStream("whitelist.json");
        BufferedReader reader;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(configf)));
			config = JsonParser.parseReader(reader).getAsJsonObject();
	        reader.close();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

        db = Database.of(config.get("mongodb").getAsString().replace("<password>", mongoKey), config.get("dbname").getAsString());
        uptime = System.currentTimeMillis();
        
        shutdown = new AtomicBoolean();
        serverCount = new AtomicInteger();
        
        data = new BotData();
    }

    public void start() {
    	DiscordClientBuilder.create(clientSecret)
    		.build()
            .withGateway(client->{
            	this.client = client;
            	
            	new Thread(()->{
                	int oldRecount = 0;
                	while (!shutdown.get()) {
                		try {
                			Thread.sleep(5000); //TODO: Use System.currentTimeMillis instead
                		} catch (Exception e) {e.printStackTrace();}
                		if (oldRecount!=serverCount.get()) {
                			oldRecount = serverCount.get();
                			onRecount(oldRecount);
                		}
                	}
                }).start();
                
                AtomicInteger botGuildCount = new AtomicInteger();
                
                MessageCreateEventHandler mcehandler = new MessageCreateEventHandler(this);
		        ReactionAddEventHandler raeh = new ReactionAddEventHandler(this);
		        ReactionRemoveEventHandler rreh = new ReactionRemoveEventHandler(this);

                EventDispatcher dispatcher = client.getEventDispatcher();

                Flux<?> m1 = dispatcher.on(ReadyEvent.class).doOnNext(e->{
                    System.out.println("Bot running at xxx");
                    //shard.uptime = System.currentTimeMillis();
                    
                    serverCount.set(serverCount.get()-botGuildCount.get());
                    botGuildCount.set(0);
                    
                    onWakeup();
                });
                Flux<?> m2 = dispatcher.on(ReconnectEvent.class).doOnNext(e->{
                    //shard.uptime = System.currentTimeMillis();
                });
                
                Flux<?> m3 = dispatcher.on(GuildCreateEvent.class).doOnNext(e -> {
		        	serverCount.incrementAndGet();
		        	botGuildCount.incrementAndGet();
		        });
                Flux<?> m4 = dispatcher.on(GuildDeleteEvent.class).doOnNext(e -> {
		        	serverCount.decrementAndGet();
		        	botGuildCount.decrementAndGet();
		        });

                Flux<?> m5 = dispatcher.on(MessageCreateEvent.class)
                	.publishOn(Schedulers.boundedElastic())
                	.flatMap(e->mcehandler.handle(e));
                
                Flux<?> m6 = dispatcher.on(MemberJoinEvent.class)
	        		.flatMap(e->{
	        			//TODO: Different rolesets for bots
	        			return e.getGuild().flatMap(guild->{
	        				return GuildAdapter.of(this, guild).getDocument()
			        			.map(document->document.getObject())
			        			.flatMap(obj->{
			        				ArrayList<Long> roles = new ArrayList<Long>();
			        				String welcomeMessage = obj.getOrDefaultString("wmsg", null);
			        				long welcomeMessageChannel = obj.getOrDefaultLong("wmsgc", -1L);
			        				
		        					DBArray rolesa = obj.getOrDefaultArray("aroles", null);
		        					if (rolesa!=null) for (int i=0; i<rolesa.getLength(); i++)
		        						roles.add(rolesa.getLong(i));
			        				
			        				Mono<Void> mono = Mono.empty();
			        				if (welcomeMessage!=null&&welcomeMessageChannel!=-1) {
			        					mono=mono.and(
			        						guild.getChannelById(Snowflake.of(welcomeMessageChannel)).cast(MessageChannel.class)
			        						.flatMap(m->m.createMessage(welcomeMessage)));
			        				}
			        				for (long role: roles) mono=mono.and(e.getMember().addRole(Snowflake.of(role)));
			        				
			        				return mono;
			        			});
	        			});
	        		});
                
                Flux<?> m7 = dispatcher.on(MemberLeaveEvent.class)
	        		.flatMap(e->{
	        			return e.getGuild().flatMap(guild->{
	        				return GuildAdapter.of(this, guild).getDocument()
		        				.map(document->document.getObject())
		        				.flatMap(obj->{
			        				String leaveMessage = obj.getOrDefaultString("lmsg", null);
			        				long leaveMessageChannel = obj.getOrDefaultLong("lmsgc", -1L);
			        				
			        				Mono<Void> mono = Mono.empty();
			        				if (leaveMessage!=null&&leaveMessageChannel!=-1L) {
			        					mono=mono.and(
			        						guild.getChannelById(Snowflake.of(leaveMessageChannel)).cast(MessageChannel.class)
			        						.flatMap(m->m.createMessage(leaveMessage)));
			        				}
			        				
			        				return mono;
			        			});
	        			});
	        		})
	        		;
                
                Flux<?> m8 = dispatcher.on(ReactionAddEvent.class)
	    	        .flatMap(e->raeh.handle(e));
                
                Flux<?> m9 = dispatcher.on(ReactionRemoveEvent.class)
                	.flatMap(e->rreh.handle(e));
                
                Flux<?> m10 = dispatcher.on(TypingStartEvent.class)
                	.flatMap(e->{
                		//TODO: Lock the channel to muted users
                		return e.getUser()
                			.filter(u->!u.isBot())
                			.flatMapMany(u->ChannelAdapter.of(this, e.getChannelId().asLong()).getDocument().flatMapMany(doc->{
                				DBObject obj = doc.getObject();
	                            if (!(obj.has("data")&&obj.getOrDefaultObject("data", null).has("chatlinkid"))) return Flux.empty();
	                            return Mono.just(obj.getOrDefaultObject("data", null).getOrDefaultLong("chatlinkid", -1L)).flatMapMany(s->{
		                        	ChatLinkAdapter adapter = ChatLinkAdapter.of(this, s);
		                        	return adapter.onEachChannel(e.getChannelId().asLong(), u.getId().asLong(), (channel, id)->channel.type());
		                        });
	                        }));
                	})
                	.onErrorContinue((e, o)->Flux.empty());
                
                Flux<?> m11 = dispatcher.on(MessageDeleteEvent.class)
                	.flatMap(e->{
                		if (!e.getMessage().isPresent()) return Mono.empty();
                		return e.getChannel().flatMap(channel->{
                			if (!(channel instanceof GuildMessageChannel)) return Mono.empty();
                			long guildID = ((GuildMessageChannel) channel).getGuildId().asLong();
                			return GuildAdapter.of(this, guildID).getDocument().flatMap(doc->{
                				DBObject obj = doc.getObject();
                				if (!obj.has("logging")) return Mono.empty();
                				return client.getChannelById(Snowflake.of(obj.getOrDefaultLong("logging", -1L)));
                			});
                		}).cast(GuildMessageChannel.class).flatMap(channel->{
                			if (!e.getMessage().isPresent()) return Mono.empty();
                			Message msg = e.getMessage().get();
                			if (!msg.getAuthor().isPresent()) return Mono.empty();
                			User author = msg.getAuthor().get();
                			if (author.isBot()) return Mono.empty();
            				return channel.createEmbed(embed->{
            					embed.setTitle("Message Deleted"); //TODO: Localize, which is trickier without data
            					embed.setDescription(msg.getContent().replace("@", "@\u200B"));
        						embed.setAuthor(author.getUsername()+"#"+author.getDiscriminator(), null, author.getAvatarUrl());
        						embed.setFooter("User ID: "+author.getId().asString(), null);
            				});
            			});
                	});
                
                Flux<?> m12 = dispatcher.on(MessageUpdateEvent.class)
                	.flatMap(e->{
                		if (!e.isContentChanged()) return Mono.empty();
                		return e.getChannel().flatMap(channel->{
                			if (!(channel instanceof GuildMessageChannel)) return Mono.empty();
                			long guildID = ((GuildMessageChannel) channel).getGuildId().asLong();
                			return GuildAdapter.of(this, guildID).getDocument().flatMap(doc->{
                				DBObject obj = doc.getObject();
                				if (!obj.has("logging")) return Mono.empty();
                				return client.getChannelById(Snowflake.of(obj.getOrDefaultLong("logging", -1L)));
                			});
                		}).cast(GuildMessageChannel.class).flatMap(channel->{
                			return e.getMessage().flatMap(msg->{
	                			if (!msg.getAuthor().isPresent()) return Mono.empty();
	                			User author = msg.getAuthor().get();
	                			if (author.isBot()) return Mono.empty();
	            				return channel.createEmbed(embed->{
	            					embed.setTitle("Message Modified"); //TODO: Localize, which is trickier without data
	            					String nmsg = e.getOld().isPresent()?
	            						"**Old Message:** "+e.getOld().get().getContent().replace("@", "@\u200B"):
	            						"**Old Message Unavailable**";
	            					embed.setDescription(nmsg+"\n**New Message:** "+msg.getContent().replace("@", "@\u200B"));
	        						embed.setAuthor(author.getUsername()+"#"+author.getDiscriminator(), null, author.getAvatarUrl());
	        						embed.setFooter("User ID: "+author.getId().asString(), null);
	            				});
                			});
            			});
                	});
                	
                return r(Mono.when(r(m1), r(m2), r(m3), r(m4), r(m5), r(m6), r(m7), r(m8), r(m9), r(m10), r(m11), r(m12)));
            }).block();
    }
    
    public Publisher<?> r(Flux<?> flux) {
    	return flux.onErrorResume(e->{
    		e.printStackTrace();
    		return Mono.empty();
    	});
    }
    public Publisher<?> r(Mono<?> flux) {
    	return flux.onErrorResume(e->{
    		e.printStackTrace();
    		return Mono.empty();
    	});
    }
    
    public void onWakeup() {
    	client.updatePresence(Presence.online(Activity.playing("sleepyhead"))).subscribe();
    }
    private void onRecount(int c) {
    	client.updatePresence(Presence.online(Activity.watching(c+" server"+(c!=1?"s":"")+" | luwu help | Luwu!"))).subscribe();
    	UnirestUtil.post("https://botblock.org/api/count", req->{
    		JsonObject body = new JsonObject();
    		body.addProperty("server_count", c);
    		body.addProperty("bot_id", String.valueOf(clientID));
    		if (config.has("botsgg_key")) body.addProperty("discord.bots.gg", config.get("botsgg_key").getAsString());
    		if (config.has("bod_key")) body.addProperty("bots.ondiscord.xyz", config.get("bod_key").getAsString());
    		if (config.has("dblcom_key")) body.addProperty("discordbotlist.com", config.get("dblcom_key").getAsString());
    		if (config.has("topgg_key")) body.addProperty("top.gg", config.get("topgg_key").getAsString());
    		
    		return req
    			.header("Content-Type", "application/json")
    			.body(body.toString());
    	}).subscribe();
    }
}