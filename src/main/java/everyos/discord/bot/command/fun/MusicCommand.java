package everyos.discord.bot.command.fun;

import java.util.HashMap;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;

import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.MessageChannel;
import everyos.discord.bot.adapter.GuildAdapter;
import everyos.discord.bot.adapter.MemberAdapter;
import everyos.discord.bot.adapter.MusicAdapter;
import everyos.discord.bot.adapter.TopEntityAdapter;
import everyos.discord.bot.command.CommandData;
import everyos.discord.bot.command.ICommand;
import everyos.discord.bot.localization.Localization;
import everyos.discord.bot.localization.LocalizedString;
import everyos.discord.bot.parser.ArgumentParser;
import everyos.discord.bot.util.MusicUtil;
import everyos.discord.bot.util.TimeUtil;
import reactor.core.publisher.Mono;

public class MusicCommand implements ICommand {
	private HashMap<Localization, HashMap<String, ICommand>> lcommands;
	public MusicCommand() {
		HashMap<String, ICommand> commands;
        lcommands = new HashMap<Localization, HashMap<String, ICommand>>();

        //Commands
        ICommand musicPlayCommand = new MusicPlayCommand();
        ICommand musicStopCommand = new MusicStopCommand();
        ICommand musicSkipCommand = new MusicSkipCommand();
        ICommand musicShuffleCommand = new MusicShuffleCommand();
        ICommand musicPauseCommand = new MusicPauseCommand();
        ICommand musicUnpauseCommand = new MusicUnpauseCommand();
        ICommand musicNpCommand = new MusicNpCommand();
        ICommand musicRepeatCommand = new MusicRepeatCommand();

        //en_US
        commands = new HashMap<String, ICommand>();
        commands.put("play", musicPlayCommand);
        commands.put("stop", musicStopCommand);
        commands.put("skip", musicSkipCommand);
        commands.put("shuffle", musicShuffleCommand);
        commands.put("pause", musicPauseCommand);
        commands.put("unpause", musicUnpauseCommand);
        commands.put("np", musicNpCommand);
        commands.put("nowplaying", musicNpCommand);
        commands.put("repeat", musicRepeatCommand);
        lcommands.put(Localization.en_US, commands);
	}
	
	@Override public Mono<?> execute(Message message, CommandData data, String argument) {
        if (argument.equals(""))
        	return message.getChannel().flatMap(c->c.createMessage(data.locale.localize(LocalizedString.NoSuchSubcommand)));

        String cmd = ArgumentParser.getCommand(argument);
        String arg = ArgumentParser.getArgument(argument);
        
        ICommand command = lcommands.get(data.locale.locale).get(cmd);
        
        if (command==null)
        	return message.getChannel().flatMap(c->c.createMessage(data.locale.localize(LocalizedString.NoSuchSubcommand)));
	    
        return command.execute(message, data, arg);
    }
}

abstract class GenericMusicCommand implements ICommand {
	@Override public Mono<?> execute(Message message, CommandData data, String argument) {
			return message.getChannel().flatMap(channel->{
				//Can probably just be message.getAuthorAsMember, but to be on the safe side...
				TopEntityAdapter teadapter = TopEntityAdapter.of(data.shard, channel);
	            if (!teadapter.isOfGuild()) return Mono.empty();
	            MemberAdapter madapter = MemberAdapter.of((GuildAdapter) teadapter.getPrimaryAdapter(), message.getAuthor().get().getId().asString());
	            
	            message.suppressEmbeds(true).subscribe();
	            return madapter.getMember()
	            	.flatMap(m->MusicAdapter.getFromMember(data.shard, m))
	            	.flatMap(ma->execute(message, data, argument, ma, channel));
	            });
			
		}
	
	abstract Mono<?> execute(Message message, CommandData data, String argument, MusicAdapter ma, MessageChannel channel);
	
	public boolean requiresDJ() {return false;}
}

class MusicPlayCommand extends GenericMusicCommand {
	@Override public Mono<?> execute(Message message, CommandData data, String argument, MusicAdapter ma, MessageChannel channel) {
		return MusicUtil.lookup(ma.getPlayer(), argument).flatMap(track->{
			return ma.queue(track, 0).flatMap(o->channel.createEmbed(embed->{
				AudioTrackInfo info = track.getInfo();
				embed.setAuthor(data.safe(info.author), info.uri, null);
				embed.setTitle(data.safe(info.title));
                embed.setDescription("Song added to queue");
                embed.addField("Length", TimeUtil.formatTime(info.length), false);
			}));
		}); 
	}
}

class MusicStopCommand extends GenericMusicCommand {
	@Override public Mono<?> execute(Message message, CommandData data, String argument, MusicAdapter ma, MessageChannel channel) {
		ma.stop();
		return channel.createMessage(data.localize(LocalizedString.MusicStopped));
	}
}

class MusicSkipCommand extends GenericMusicCommand {
	@Override public Mono<?> execute(Message message, CommandData data, String argument, MusicAdapter ma, MessageChannel channel) {
		ma.skip(Integer.valueOf(argument)); //TODO: Catch exceptions
		return channel.createMessage(data.localize(LocalizedString.TrackSkipped));
	}
}

class MusicShuffleCommand extends GenericMusicCommand {
	@Override public Mono<?> execute(Message message, CommandData data, String argument, MusicAdapter ma, MessageChannel channel) {
		ma.shuffle();
		return channel.createMessage(data.localize(LocalizedString.QueueShuffled));
	}
}

class MusicPauseCommand extends GenericMusicCommand {
	@Override public Mono<?> execute(Message message, CommandData data, String argument, MusicAdapter ma, MessageChannel channel) {
		ma.setPaused(true);
		return channel.createMessage(data.localize(LocalizedString.MusicPaused));
	}
}

class MusicUnpauseCommand extends GenericMusicCommand {
	@Override public Mono<?> execute(Message message, CommandData data, String argument, MusicAdapter ma, MessageChannel channel) {
		ma.setPaused(false);
		return channel.createMessage(data.localize(LocalizedString.MusicUnpaused));
	}
}

class MusicNpCommand extends GenericMusicCommand {
	@Override public Mono<?> execute(Message message, CommandData data, String argument, MusicAdapter ma, MessageChannel channel) {
		AudioTrack np = ma.getPlaying();
		if (np==null) return channel.createMessage(data.localize(LocalizedString.NoTrackPlaying));
        return channel.createEmbed(embed->{
            AudioTrackInfo info = np.getInfo();
            embed.setAuthor(data.safe(info.author), info.uri, null);
            embed.setTitle(data.safe(info.title));
            embed.setDescription("Now playing");
            embed.addField("Length", 
                TimeUtil.formatTime(np.getPosition())+"/"+ TimeUtil.formatTime(info.length)+
                " ("+(Math.floor((((double) np.getPosition())/(double) info.length)*1000.)/10.)+"%)", false);     
        });
	}
}

class MusicRepeatCommand extends GenericMusicCommand {
	@Override public Mono<?> execute(Message message, CommandData data, String argument, MusicAdapter ma, MessageChannel channel) {
		ma.setRepeat(!ma.getRepeat());
		if (ma.getRepeat()) return channel.createMessage(data.localize(LocalizedString.MusicRepeatSet));
		return channel.createMessage(data.localize(LocalizedString.MusicRepeatUnset));
	}
}

/*
public class MusicCommand implements ICommand {
	@Override public void execute(Message message, String argument) {
		//search, set time
	    if (args[0].equals("queue")) {
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
	                        embed.addField("Track "+(i+1), "**Title:** "+MessageHelper.filter(track), false);
	                    }
	                }
	            });
	        }
	    }
	}
}
*/