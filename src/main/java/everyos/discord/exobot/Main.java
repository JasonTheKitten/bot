package everyos.discord.exobot;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.track.playback.NonAllocatingAudioFrameBuffer;

import discord4j.core.DiscordClient;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.event.EventDispatcher;
import discord4j.core.event.domain.PresenceUpdateEvent;
import discord4j.core.event.domain.guild.GuildCreateEvent;
import discord4j.core.event.domain.guild.GuildDeleteEvent;
import discord4j.core.event.domain.guild.MemberJoinEvent;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.lifecycle.ReconnectEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.event.domain.message.MessageUpdateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.Message.Type;
import discord4j.core.object.presence.Activity;
import discord4j.core.object.presence.Presence;
import discord4j.core.object.presence.Status;
import everyos.discord.exobot.cases.ChannelCase;
import everyos.discord.exobot.cases.ChatLinkChannelCaseData;
import everyos.discord.exobot.commands.BanCommand;
import everyos.discord.exobot.commands.BotExcludeCommand;
import everyos.discord.exobot.commands.BotIncludeCommand;
import everyos.discord.exobot.commands.ChannelLinkCommand;
import everyos.discord.exobot.commands.CurrencyCommand;
import everyos.discord.exobot.commands.EchoCommand;
import everyos.discord.exobot.commands.HelpCommand;
import everyos.discord.exobot.commands.ICommand;
import everyos.discord.exobot.commands.IdentifierCommand;
import everyos.discord.exobot.commands.IncrementCommand;
import everyos.discord.exobot.commands.InvalidCommand;
import everyos.discord.exobot.commands.KickCommand;
import everyos.discord.exobot.commands.LogSaveCommand;
import everyos.discord.exobot.commands.MusicCommand;
import everyos.discord.exobot.commands.OptCommand;
import everyos.discord.exobot.commands.PrefixCommand;
import everyos.discord.exobot.commands.PurgeAfterCommand;
import everyos.discord.exobot.commands.PurgeCommand;
import everyos.discord.exobot.commands.RemindCommand;
import everyos.discord.exobot.commands.SentenceGameCommand;
import everyos.discord.exobot.commands.StyleRoleCommand;
import everyos.discord.exobot.commands.SuggestionConfigCommand;
import everyos.discord.exobot.commands.TwitchConfigCommand;
import everyos.discord.exobot.commands.UnbanCommand;
import everyos.discord.exobot.commands.UnoptCommand;
import everyos.discord.exobot.commands.WelcomeCommand;
import everyos.discord.exobot.objects.ChannelObject;
import everyos.discord.exobot.objects.GlobalUserObject;
import everyos.discord.exobot.objects.GuildObject;
import everyos.discord.exobot.objects.MusicObject;
import everyos.discord.exobot.objects.ReminderObject;
import everyos.discord.exobot.objects.UserObject;
import everyos.discord.exobot.util.ChannelHelper;
import everyos.discord.exobot.util.CommandHelper;
import everyos.discord.exobot.util.GuildHelper;
import everyos.discord.exobot.util.MessageHelper;
import everyos.discord.exobot.util.StringUtil;
import everyos.discord.exobot.util.UserHelper;
import everyos.discord.exobot.webserver.WebServer;

public class Main {
	private static DiscordClient client;

	public static void main(String[] rargs) throws Exception {
        System.out.println("Command is running");
        AtomicInteger servercount = new AtomicInteger();

        File keys = new File(StaticFunctions.getAppData("keys.config"));

        String[] args;
        if (rargs.length < 2) {
            args = new String[2];

            System.out.println("User Client ID and Bot Token both expected");
            Scanner s;
            if (keys.exists()) {
                s = new Scanner(keys);
            } else
                try {
                    s = new Scanner(System.in);
                } catch (NoSuchElementException e) {
                    System.out.println("Could not start prompt");
                    return;
                }
            ;
            System.out.println("Enter Client ID:");
            args[0] = s.next();
            System.out.println("Enter Bot Token:");
            args[1] = s.next();
            s.close();
        } else {
            args = rargs;
        }

        if (!keys.exists()) {
            keys.getParentFile().mkdirs();
            BufferedWriter writer = new BufferedWriter(
                    new FileWriter(StaticFunctions.getAppData(StaticFunctions.keysFile)));
            writer.write(args[0] + " " + args[1]);
            writer.close();
        }

        HashMap<String, ICommand> commands = new HashMap<String, ICommand>();
        Statics.commands = commands;
        Statics.guilds = new HashMap<String, GuildObject>();
        Statics.users = new HashMap<String, GlobalUserObject>();
        Statics.twitchchannels = new HashMap<String, String>();
        Statics.servers = new ArrayList<WebServer>();
        Statics.musicChannels = new ArrayList<MusicObject>();

        CommandHelper.register("help", new HelpCommand());
        CommandHelper.register("echo", new EchoCommand());
        CommandHelper.register("prefix", new PrefixCommand());
        CommandHelper.register("suggestions", new SuggestionConfigCommand());
        CommandHelper.register("opt", new OptCommand());
        CommandHelper.register("unopt", new UnoptCommand());
        CommandHelper.register("botinclude", new BotIncludeCommand());
        CommandHelper.register("botexclude", new BotExcludeCommand());
        CommandHelper.register("twitchconfig", new TwitchConfigCommand());
        CommandHelper.register("kick", new KickCommand());
        CommandHelper.register("ban", new BanCommand());
        CommandHelper.register("unban", new UnbanCommand());
        CommandHelper.register("purge", new PurgeCommand());
        CommandHelper.register("remind", new RemindCommand());
        CommandHelper.register("logsave", new LogSaveCommand());
        CommandHelper.register("purgeafter", new PurgeAfterCommand());
        CommandHelper.register("increment", "inc", new IncrementCommand());
        CommandHelper.register("oneword", new SentenceGameCommand());
        CommandHelper.register("currency", "feth", new CurrencyCommand());
        CommandHelper.register("addstylerole", new StyleRoleCommand());
        CommandHelper.register("music", "m", new MusicCommand());
        CommandHelper.register("chatlink", new ChannelLinkCommand());
        CommandHelper.register("channelidentify", new IdentifierCommand());
        CommandHelper.register("welcome", new WelcomeCommand());

        InvalidCommand invalidCommand = new InvalidCommand();

        AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
        playerManager.getConfiguration().setFrameBufferFactory(NonAllocatingAudioFrameBuffer::new);
        AudioSourceManagers.registerRemoteSources(playerManager);
        Statics.playerManager = playerManager;

        final DiscordClient client = new DiscordClientBuilder(args[1]).build();
        Main.client = client;

        Statics.client = client;
        StaticFunctions.load();

        EventDispatcher dispatcher = client.getEventDispatcher();
        dispatcher.on(ReadyEvent.class).subscribe(ready -> {
        	System.out.println("Bot running at https://discordapp.com/oauth2/authorize?&client_id=" +
            		args[0] + "&scope=bot&permissions=8");
        	servercount.set(0);
        	onRecount(0);
            Statics.musicChannels.forEach(ch -> ch.join());
        });
        dispatcher.on(ReconnectEvent.class).subscribe(ready -> {
        	Statics.musicChannels.forEach(ch -> ch.join());
        });
        dispatcher.on(GuildCreateEvent.class).subscribe(e -> {
        	onRecount(servercount.incrementAndGet());
        });
        dispatcher.on(GuildDeleteEvent.class).subscribe(e -> {
        	onRecount(servercount.decrementAndGet());
        });

        dispatcher.on(MessageCreateEvent.class).subscribe(messageevent -> {
            Message message = messageevent.getMessage();
            try {
                if (!messageevent.getMember().isPresent() || messageevent.getMember().get().isBot())
                    return;

                GuildObject guild = GuildHelper.getGuildData(message.getGuild());

                UserObject invoker = UserHelper.getUserData(guild, message.getAuthorAsMember());
                invoker.money += guild.chatmoney;
                StaticFunctions.save();

                ChannelCase.CASES special = ChannelCase.getSpecial(guild, message.getChannel().block());
                if (special != ChannelCase.CASES.NULL) {
                    ChannelCase.execute(special, message);
                    return;
                }
                if (!(message.getType() == Type.DEFAULT)) {
                    return;
                }
                String content = message.getContent().orElse("");
                String prefix = GuildHelper.getGuildData(message.getGuild()).prefix;
                if (content.startsWith(prefix)) {
                    String msg = StringUtil.sub(content, prefix.length());
                    int space = msg.indexOf(" ");
                    if (space <= 0)
                        space = content.length();
                    if (space <= 0)
                        space = 1;
                    String cmd = StringUtil.sub(msg, 0, space);
                    String argstr = StringUtil.sub(msg, space + 1, content.length());

                    ICommand exe = commands.getOrDefault(cmd, invalidCommand);
                    exe.execute(message, argstr);
                }
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    MessageHelper.send(message.getChannel(), e.getClass().getSimpleName() + ":" + e.getMessage(), true);
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
        });
        dispatcher.on(MessageUpdateEvent.class).subscribe(messageevent -> {
            Message message = messageevent.getMessage().block();
            try {
                GuildObject guild = GuildHelper.getGuildData(message.getGuild());

                ChannelCase.CASES special = ChannelCase.getSpecial(guild, message.getChannel().block());
                if (special == ChannelCase.CASES.CHATLINK) {
                    ChatLinkChannelCaseData.send(message, true);
                }
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    MessageHelper.send(message.getChannel(), e.getClass().getSimpleName() + ":" + e.getMessage(), true);
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
        });

        dispatcher.on(PresenceUpdateEvent.class).subscribe(e->{
            e.getMember().subscribe(member -> {
                member.getPresence().subscribe(presence->{
                    Status status = presence.getStatus();
                    if (status==Status.OFFLINE||status==Status.DO_NOT_DISTURB) return;

                    ArrayList<ReminderObject> reminders = UserHelper.getGlobalUserData(member).reminders;

                    synchronized(reminders) {
                        int i = 0;
                        while (i<reminders.size()) {
                            if (reminders.get(i).timestamp==-1) {
                                reminders.get(i).send();
                                reminders.remove(i);
                            } else i++;
                        }
                    }
                });
            });
        });

        dispatcher.on(MemberJoinEvent.class).subscribe(e->{
            e.getGuild().subscribe(rawguild->{
                GuildObject guild = GuildHelper.getGuildData(rawguild);
                if ((guild.welcomeChannelID!=null)&&(guild.welcomeMessage!=null)) {
                    ChannelObject channel = ChannelHelper.getChannelData(guild, guild.welcomeChannelID);
                    channel.send(guild.welcomeMessage
                        .replace("{user.ping}", e.getMember().getMention())
                        .replace("{user.name}", e.getMember().getUsername())
                        .replace("{+", "{")  
                    , true);
                }
            });
        });
        
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override public void run() {
                client.logout().subscribe();
				Statics.servers.forEach(k->{
					k.close();
				});
            }
        });
		
		client.login().block();
	}
	
	private static void onRecount(int c) {
    	client.updatePresence(Presence.online(Activity.watching("spiders make webs ("+c+" server"+(c!=1?"s":"")+")"))).subscribe();
    }
}
