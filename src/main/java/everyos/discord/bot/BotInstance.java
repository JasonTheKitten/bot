package everyos.discord.bot;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import discord4j.core.DiscordClient;
import discord4j.core.event.EventDispatcher;
import discord4j.core.event.domain.guild.GuildCreateEvent;
import discord4j.core.event.domain.guild.GuildDeleteEvent;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.lifecycle.ReconnectEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.shard.ShardingClientBuilder;
import everyos.discord.bot.adapter.ChannelAdapter;
import everyos.discord.bot.adapter.GuildAdapter;
import everyos.discord.bot.adapter.MemberAdapter;
import everyos.discord.bot.channelcase.ChatLinkChannelCase;
import everyos.discord.bot.channelcase.DefaultChannelCase;
import everyos.discord.bot.channelcase.SuggestionsChannelCase;
import everyos.discord.bot.command.CommandData;
import everyos.discord.bot.command.ICommand;
import everyos.discord.bot.localization.Localization;
import everyos.discord.bot.localization.LocalizationProvider;
import everyos.storage.database.Database;
import everyos.storage.database.FileUtil;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class BotInstance {
    public static void main(String args[]) {
        System.out.println("Command is running");

        String clientID; String clientSecret;
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
        
        new BotInstance(clientID, clientSecret).start();
    }

    public String clientID;
    private String clientSecret;

    public Database db;
    public long uptime;
	public AtomicBoolean shutdown;
	public AtomicInteger serverCount;

    public BotInstance(String id, String secret) {
        clientID = id;
        clientSecret = secret;

        db = Database.of("somepath");
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

                HashMap<String, ICommand> channels = new HashMap<String, ICommand>();
                channels.put("default", new DefaultChannelCase());
                channels.put("suggestions", new SuggestionsChannelCase());
                channels.put("chatlink", new ChatLinkChannelCase());

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

                dispatcher.on(MessageCreateEvent.class)
                    .map(e->e.getMessage())
                    .filter(message->message.getAuthor().isPresent()&&!message.getAuthor().get().isBot())
                    .doOnNext(message->{
                        message
                            .getChannel()
                            .doOnError(e->e.printStackTrace())
                            .flatMap(channel->GuildAdapter.of(shard, channel))
                            .doOnNext(adapter->{
                                String uid = message.getAuthor().get().getId().asString();
                                MemberAdapter.of(adapter, uid).getDocument().getObject((object, document)->{
                                    long time = 1000*60;
                                    long curtime = System.currentTimeMillis();
                                    if (curtime-object.getOrDefaultLong("lastfeth", -time)>=time) {
                                        object.set("feth", object.getOrDefaultLong("feth", 0)+1);
                                        object.set("lastfeth", curtime);
                                        document.save();
                                    }
                                });
                            })
                            .onErrorResume(e->{e.printStackTrace(); return Mono.empty();})
                            .subscribe();
                    })
                    .map(message->{
                        return message;
                    })
                    .flatMap(message->{
                        AtomicReference<String> cmode = new AtomicReference<String>();
                        new ChannelAdapter(shard, message.getChannelId().asString()).getDocument().getObject(obj->{
                            cmode.set(obj.getOrDefaultString("type", "default"));
                        });
                        LocalizationProvider provider = new LocalizationProvider(Localization.en_US);
                        CommandData data = new CommandData(provider, shard);

                        return
                            channels.getOrDefault(cmode.get(), channels.get("default"))
                                .execute(message, data, message.getContent().orElse(""))
                            .onErrorResume(e->{e.printStackTrace(); return Mono.empty();});
                    })
                    .onErrorResume(e->{e.printStackTrace(); return Flux.empty();})
                    .subscribe();
                    
            })
            .flatMap(DiscordClient::login).blockLast();
    }
}