package everyos.discord.exobot.commands;

import java.util.LinkedList;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;

import discord4j.core.object.entity.Message;
import everyos.discord.exobot.StaticFunctions;
import everyos.discord.exobot.objects.ChannelObject;
import everyos.discord.exobot.objects.GuildObject;
import everyos.discord.exobot.objects.MusicObject;
import everyos.discord.exobot.objects.PlaylistObject;
import everyos.discord.exobot.objects.UserObject;
import everyos.discord.exobot.objects.VoiceChannelObject;
import everyos.discord.exobot.util.ChannelHelper;
import everyos.discord.exobot.util.GuildHelper;
import everyos.discord.exobot.util.StringUtil;
import everyos.discord.exobot.util.TimeUtil;
import everyos.discord.exobot.util.UserHelper;

public class MusicCommand implements ICommand {
    @Override public void execute(Message message, String argument) {
		GuildObject guild = GuildHelper.getGuildData(message.getGuild());
		ChannelObject channel = ChannelHelper.getChannelData(guild, message.getChannel().block());
		
		String[] args = StringUtil.split(argument, " ");
		if (args.length==0) {
			channel.send("Expected at least one parameter", true); return;
        }

        message.suppressEmbeds(true).subscribe();

        UserObject invoker = UserHelper.getUserData(guild, message.getAuthorAsMember());

        if (args[0].equals("play")) {
            if (args.length<2) {
                channel.send("Subcommand expected one parameter", true); return;
            }

            MusicObject music = getMusicChannel(guild, channel);
            if (music==null) return;

            playTrack(music, channel, args[1]);
        } else if (args[0].equals("repeat")) {
        	if (args.length<2) {
                channel.send("Subcommand expected one parameter", true); return;
            }
        	
        	MusicObject music = getMusicChannel(guild, channel);
            if (music==null) return;
            
            boolean rep = args[1].toLowerCase().equals("true")||args[1].toLowerCase().equals("on");
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
        } else if (args[0].equals("pause")||args[0].equals("unpause")) {
        	MusicObject music = getMusicChannel(guild, channel);
            if (music==null) return;
            
            music.setPaused(args[0].equals("pause"));
            channel.send("Music paused!", true);
        } else if (args[0].equals("shuffle")) {
        	MusicObject music = getMusicChannel(guild, channel);
            if (music==null) return;
            
            music.shuffle();
            channel.send("Queue shuffled!", true);
        } else if (args[0].equals("nowplaying")||args[0].equals("np")) {
        	MusicObject music = getMusicChannel(guild, channel);
            if (music==null) return;
            
            LinkedList<AudioTrack> queue = music.getQueue();
            if (queue.size()==0) {
                channel.send("Nothing is playing right now!", true); return;
            }
            AudioTrack np = queue.get(0);
            channel.send(embed->{
                AudioTrackInfo info = np.getInfo();
                embed.setAuthor(info.author, info.uri, null);
                embed.setTitle(info.title);
                embed.setDescription("Now playing");
                embed.addField("Length", 
                    TimeUtil.formatTime(Math.floor(np.getPosition()/1000))+"/"+
                    TimeUtil.formatTime(Math.floor(info.length/1000))+" ("+
                    Math.floor(((long)np.getPosition()/(long)info.length)*1000L)/10L+"%)", false);
                
            });
        } else if (args[0].equals("queue")) {
        	MusicObject music = getMusicChannel(guild, channel);
            if (music==null) return;
            
            LinkedList<AudioTrack> queue = music.getQueue();
            if (queue.size()==0) {
                channel.send("The queue appears empty. Why not add some music?", true); return;
            }
            channel.send(embed->{
                embed.setTitle("Music Queue");
                Long ttime = 0L;
                synchronized(queue) {
                    for (int i=0; i<queue.size(); i++) {
                        AudioTrack track = queue.get(i);
                        embed.addField("Track "+(i+1), "**Title:** "+track.getInfo().title+"\n"+
                            "**Length:** "+TimeUtil.formatTime(Math.floor(track.getDuration()/1000)), false);

                        ttime+=track.getDuration();
                    }
                }
                embed.setFooter("Total length: "+TimeUtil.formatTime(Math.floor(ttime/1000)), null);
            });
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
        } else if (args[0].equals("playlist")) {
            if (args.length<2) {
                channel.send("Subcommand expected at least one argument!\n"+
                "<playlist>[args] Subcommands on playlists include create, add, delete, and details\n"+
                "Select playlist without subcommand to play it", true); return;
            }
            if (args.length==2) {
                MusicObject music = getMusicChannel(guild, channel);
                if (music==null) return;

                PlaylistObject playlist = invoker.toGlobal().getPlaylist(args[1], false);
                if (playlist == null) {
                    channel.send("Please create the playlist first!", true); return;
                }

                playlist.playlist.forEach(uri->playTrack(music, channel, uri));
            } else if (args[2].equals("create")) {
                invoker.toGlobal().getPlaylist(args[1], true);
                channel.send("Created playlist!", true);
            } else if (args[2].equals("delete")) {
                invoker.toGlobal().playlists.remove(args[1]);
                StaticFunctions.save();
                channel.send("Deleted playlist!", true);
            } else if (args[2].equals("add")) {
                PlaylistObject playlist = invoker.toGlobal().getPlaylist(args[1], false);
                if (playlist == null) {
                    channel.send("Please create the playlist first!", true); return;
                }
                if (args.length<4) {
                    channel.send("Expected URL!", true); return;
                }
                synchronized(playlist.playlist) {
                    playlist.playlist.add(args[3]);
                }
                StaticFunctions.save();
                channel.send("Added to playlist!", true);
            } else if (args[2].equals("details")) {
                PlaylistObject playlist = invoker.toGlobal().getPlaylist(args[1], false);
                if (playlist == null) {
                    channel.send("No such playlist!", true); return;
                }
                channel.send(embed->{
                    embed.setTitle("Playlist");
                    synchronized(playlist.playlist) {
                        for (int i=0; i<playlist.playlist.size(); i++) {
                            String track = playlist.playlist.get(i);
                            embed.addField("Track "+(i+1), "**Title:** "+track, false);
                        }
                    }
                });
            }
        }
	}

	private void playTrack(MusicObject music, ChannelObject channel, String uri) {
        music.play(uri, track->{
            channel.send(embed->{
                AudioTrackInfo info = track.getInfo();
                embed.setAuthor(info.author, info.uri, null);
                embed.setTitle(info.title);
                embed.setDescription("Song added to queue");
                embed.addField("Length", TimeUtil.formatTime(Math.floor(info.length/1000)), false);
            });
        }, ()->{
            channel.send("Track load failed!", true);
        });
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
        return 
            "**<command>** Can be play, queue, skip, stop, pause/unpause, nowplaying/np, shuffel, "+
            "setdefaultchannel, or setchannel\n"+
            "**[args+]** Run subcommand for additional usage";
	}
}