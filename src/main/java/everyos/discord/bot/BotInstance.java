package everyos.discord.bot;

import java.awt.Color;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

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
import discord4j.core.object.entity.Attachment;
import discord4j.core.object.entity.MessageChannel;
import discord4j.core.object.util.Snowflake;
import discord4j.core.shard.ShardingClientBuilder;
import everyos.discord.bot.adapter.GuildAdapter;
import everyos.discord.bot.adapter.MessageAdapter;
import everyos.discord.bot.event.MessageCreateEventHandler;
import everyos.storage.database.DBArray;
import everyos.storage.database.DBObject;
import everyos.storage.database.Database;
import everyos.storage.database.FileUtil;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class BotInstance {
    public static void main(String args[]) {
        System.out.println("Command is running");

        String clientID; String clientSecret;
        String giphyKey = null; String rapidKey = null;
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
        
        new BotInstance(clientID, clientSecret, giphyKey, rapidKey).start();
    }

    public String clientID;
    private String clientSecret;
    public String giphyKey;
    public String rapidKey;

    public Database db;
    public long uptime;
	public AtomicBoolean shutdown;
	public AtomicInteger serverCount;

    public BotInstance(String id, String secret, String gk, String rk) {
        clientID = id;
        clientSecret = secret;
        giphyKey = gk;
        rapidKey = rk;

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
	        				AtomicReference<String> welcomeMessage = new AtomicReference<String>();
	        				AtomicReference<String> welcomeMessageChannel = new AtomicReference<String>();
	        				ArrayList<String> roles = new ArrayList<String>();
	        				GuildAdapter.of(shard, guild).getDocument().getObject(obj->{
	        					welcomeMessage.set(obj.getOrDefaultString("wmsg", null));
	        					welcomeMessageChannel.set(obj.getOrDefaultString("wmsgc", null));
	        					
	        					DBArray rolesa = obj.getOrDefaultArray("aroles", null);
	        					if (rolesa!=null) rolesa.forEach(i->{
	        						roles.add(rolesa.getString(i));
	        					});
	        				});
	        				
	        				Mono<Void> mono = Mono.empty();
	        				if (welcomeMessage.get()!=null&&welcomeMessageChannel.get()!=null) {
	        					mono=mono.and(
	        						guild.getChannelById(Snowflake.of(welcomeMessageChannel.get())).cast(MessageChannel.class)
	        						.flatMap(m->m.createMessage(welcomeMessage.get())));
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
	        				AtomicReference<String> leaveMessage = new AtomicReference<String>();
	        				AtomicReference<String> leaveMessageChannel = new AtomicReference<String>();
	        				GuildAdapter.of(shard, guild).getDocument().getObject(obj->{
	        					leaveMessage.set(obj.getOrDefaultString("lmsg", null));
	        					leaveMessageChannel.set(obj.getOrDefaultString("lmsgc", null));
	        				});
	        				
	        				Mono<Void> mono = Mono.empty();
	        				if (leaveMessage.get()!=null&&leaveMessageChannel.get()!=null) {
	        					mono=mono.and(
	        						guild.getChannelById(Snowflake.of(leaveMessageChannel.get())).cast(MessageChannel.class)
	        						.flatMap(m->m.createMessage(leaveMessage.get())));
	        				}
	        				
	        				return mono;
	        			});
	        		})
	        		.onErrorResume(e->{e.printStackTrace(); return Flux.empty();})
	        		.subscribe();
                
                dispatcher.on(ReactionAddEvent.class)
	    	        .flatMap(e->{
	    				return e.getMessage().flatMap(message->{	
	    					String emoji =
	        					e.getEmoji().asUnicodeEmoji().map(ue->ue.getRaw())
	        					.orElseGet(()->e.getEmoji().asCustomEmoji().map(ce->ce.getId().asString()).get());
	    					
	    					//Reaction role code
	    					AtomicReference<String> roleID = new AtomicReference<String>();
	        				MessageAdapter.of(shard, message.getChannelId().asString(), message.getId().asString()).getDocument().getObject(obj->{
	        					roleID.set(obj.getOrDefaultObject("roles", new DBObject()).getOrDefaultString(emoji, null));
	        				});
	    					
	    					Mono<Void> mono = Mono.empty();
	    					if (roleID.get()!=null) {
	    						mono=mono.and(e.getMember().get().addRole(Snowflake.of(roleID.get())));
	    					}
	    					
	    					//Starboard code
	    					Snowflake gid = e.getGuildId().orElse(null);
	    					if (gid!=null) {
	    						AtomicReference<String> starID = new AtomicReference<String>();
	    						AtomicReference<String> sbCID = new AtomicReference<String>();
	    						GuildAdapter.of(shard, gid.asString()).getDocument().getObject(obj->{
	    							starID.set(obj.getOrDefaultString("star", null));
	    							sbCID.set(obj.getOrDefaultString("starc", null));
	    						});
	    						
	    						if (emoji.equals(starID.get())) {
	    							ArrayList<Long> reactors = new ArrayList<Long>();
	    							mono=mono.and(message.getReactors(e.getEmoji())
	    								.doOnNext(m->{
	    									long mid = m.getId().asLong();
	    									if (!reactors.contains(mid)) reactors.add(mid);
	    								})
	    								.count()
	    								.flatMap(n->{
	    									AtomicReference<String> orgID = new AtomicReference<String>();
	    									AtomicReference<String> orgCID = new AtomicReference<String>();
	    									AtomicReference<String> sbID = new AtomicReference<String>();
	    									AtomicReference<Mono<?>> mono2 = new AtomicReference<Mono<?>>();
	    									mono2.set(Mono.empty());
	    									
	    									String mid = e.getMessageId().asString();
	    									String mcid = e.getChannelId().asString();
	    									MessageAdapter.of(shard, mcid, mid).getDocument().getObject((obj, doc)->{
	    										if (!obj.getOrDefaultBoolean("issb", false)) { //Post is not from starboard
	    											orgID.set(mid); orgCID.set(mcid);
	    											if (obj.getOrDefaultString("sbmid", null)!=null) { //Post is already on starboard
	    												sbID.set(obj.getOrDefaultString("sbmid", null));
	    											} else { //Post is not yet on starboard
	    												if (n<1) return; //TODO: Var
	    												mono2.set(e.getGuild().flatMap(g->g.getChannelById(Snowflake.of(sbCID.get())).cast(MessageChannel.class)
	    													.flatMap(c->{
	    														return c.createMessage(msg->{
	    															msg.setEmbed(embed->{
	    																embed.setColor(Color.YELLOW);
	    																embed.setDescription(message.getContent().orElse(""));
	    																Set<Attachment> s = message.getAttachments();
	    																if (!s.isEmpty()) embed.setImage(s.iterator().next().getUrl());
	    																message.getAuthor().ifPresent(a->{
	    																	String url = String.format(
	    																		"https://discordapp.com/channels/%s/%s/%s", 
	    																		gid.asString(),
	    																		e.getChannelId().asString(),
	    																		e.getMessageId().asString());
	    																	embed.setAuthor(a.getUsername()+"#"+a.getDiscriminator(), url, a.getAvatarUrl());
	    																	embed.setFooter("Posted by User ID: "+a.getId().asString(), null);
	    																}); //TODO: Localize
	    															});
	    														});
	    													}))
	    													.flatMap(m->{
	    														sbID.set(m.getId().asString());
	    														obj.set("sbmid", sbID.get());
	    														MessageAdapter.of(shard, sbCID.get(), sbID.get()).getDocument().getObject((obj2, doc2)->{
	    															obj2.set("issb", true);
	    															doc2.save();
	    														});
	    														return Mono.empty();
	    													}));
	    											}
	    										} else { //Post is from starboard
	    											sbID.set(mid); sbCID.set(mcid);
	    											orgID.set(obj.getOrDefaultString("ogm", null));
	    										}
	    										doc.save();
	    									});
	    									return mono2.get(); //TODO: More
	    								}));
	    						}
	    					}
	    					
	    					return mono.onErrorResume(ex->{ex.printStackTrace(); return Mono.empty();});
	    				});
	    			})
	    			.onErrorResume(e->{e.printStackTrace(); return Flux.empty();})
	    			.subscribe();
                
                
            })
            .flatMap(DiscordClient::login).blockLast();
    }
}