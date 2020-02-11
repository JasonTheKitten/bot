package everyos.discord.exobot.commands;

import discord4j.core.object.entity.Message;
import everyos.discord.exobot.objects.ChannelObject;
import everyos.discord.exobot.objects.GuildObject;
import everyos.discord.exobot.objects.MusicObject;
import everyos.discord.exobot.objects.UserObject;
import everyos.discord.exobot.objects.VoiceChannelObject;
import everyos.discord.exobot.util.ChannelHelper;
import everyos.discord.exobot.util.GuildHelper;
import everyos.discord.exobot.util.StringUtil;
import everyos.discord.exobot.util.UserHelper;

public class MusicCommand implements ICommand {
	@Override public void execute(Message message, String argument) {
		GuildObject guild = GuildHelper.getGuildData(message.getGuild());
		ChannelObject channel = ChannelHelper.getChannelData(guild, message.getChannel().block());
		
		String[] args = StringUtil.split(argument, " ");
		if (args.length==0) {
			channel.send("Expected at least one parameter", true); return;
        }

        UserObject invoker = UserHelper.getUserData(guild, message.getAuthorAsMember());

        if (args[0].equals("play")) {
            if (args.length<2) {
                channel.send("Subcommand expected one parameter", true); return;
            }

            MusicObject music = getMusicChannel(guild, channel);
            if (music==null) return;

            music.play(args[1], track->{
            	channel.send("Added to queue: `"+track.getInfo().title+"`", false);
            });
        } else if (args[0].equals("playlist")) {
        	if (args.length<2) {
                channel.send("Subcommand expected one parameter", true); return;
            }
        } else if (args[0].equals("repeat")) {
        	if (args.length<2) {
                channel.send("Subcommand expected one parameter", true); return;
            }
        	
        	MusicObject music = getMusicChannel(guild, channel);
            if (music==null) return;
            
            boolean rep = args[1].toLowerCase().equals("true");
        	music.repeat(rep);
        	channel.send("Repeat mode: "+rep, true);
        } else if (args[0].equals("skip")) {        	
        	MusicObject music = getMusicChannel(guild, channel);
            if (music==null) return;
            
        	music.skip();
        	channel.send("Track skipped", true);
        } else if (args[0].equals("stop")) {
        	MusicObject music = getMusicChannel(guild, channel);
            if (music==null) return;
            
            music.stop();
        	channel.send("Queue stopped and cleared", true);
        } else if (args[0].equals("setchannel")) {
            if (!invoker.isOpted()) {
                channel.send("User is not opted to use this command!", true); return;
            }
            channel.musicChannelID = ChannelHelper.parseChannelId(args[1]);
            if (channel.musicChannelID==null) channel.musicChannelID = args[1];
		} else if (args[0].equals("setdefaultchannel")) {
            if (!invoker.isOpted()) {
                channel.send("User is not opted to use this command!", true); return;
            }
            guild.musicChannelID = ChannelHelper.parseChannelId(args[1]);
            if (guild.musicChannelID==null) guild.musicChannelID = args[1];
        }
	}

	private MusicObject getMusicChannel(GuildObject guild, ChannelObject channel) {
		String targetMusicChannel = (channel.musicChannelID==null)?guild.musicChannelID:channel.musicChannelID;
        if (targetMusicChannel==null) {
        	channel.send("Please have an opted user configure music channels", true); return null;
        }
        
        VoiceChannelObject musicChannel = ChannelHelper.getChannelData(guild, targetMusicChannel).asVoiceChannel();
        if (musicChannel == null) {
            channel.send("Hmm, something's off", true); return null;
        }
        
        MusicObject music = musicChannel.getMusicObject();
        
        return music;
	}

	@Override public String getHelp() {
		return "<command>[args+] Invokes commands on the music system";
	}

	@Override public COMMANDS getType() {
		return COMMANDS.Fun;
	}
	
	@Override public String getFullHelp() {
        return "**<command>** Can be play, skip, stop, setdefaultchannel, or setchannel\n"+
            "**[args+]** Run subcommand for additional usage";
	}
}