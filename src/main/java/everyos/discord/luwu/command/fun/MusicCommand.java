package everyos.discord.luwu.command.fun;

import java.util.HashMap;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;

import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.rest.util.Color;
import discord4j.rest.util.Permission;
import everyos.discord.luwu.adapter.MusicAdapter;
import everyos.discord.luwu.adapter.MusicAdapter.VoiceStateMissingException;
import everyos.discord.luwu.adapter.UserAdapter;
import everyos.discord.luwu.annotation.Help;
import everyos.discord.luwu.command.CategoryEnum;
import everyos.discord.luwu.command.CommandData;
import everyos.discord.luwu.command.ICommand;
import everyos.discord.luwu.command.IGroupCommand;
import everyos.discord.luwu.database.DBArray;
import everyos.discord.luwu.database.DBObject;
import everyos.discord.luwu.localization.Localization;
import everyos.discord.luwu.localization.LocalizedString;
import everyos.discord.luwu.parser.ArgumentParser;
import everyos.discord.luwu.util.BotPermissionUtil;
import everyos.discord.luwu.util.ErrorUtil.LocalizedException;
import everyos.discord.luwu.util.FillinUtil;
import everyos.discord.luwu.util.MusicUtil;
import everyos.discord.luwu.util.TimeUtil;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Help(help=LocalizedString.MusicCommandHelp, ehelp = LocalizedString.MusicCommandExtendedHelp, category=CategoryEnum.Fun)
public class MusicCommand implements IGroupCommand {
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
        ICommand musicQueueCommand = new MusicQueueCommand();
        ICommand musicPlaylistCommand = new MusicPlaylistCommand();
        ICommand musicRadioCommand = new MusicRadioCommand();
        ICommand musicVolumeCommand = new MusicVolumeCommand();
        ICommand musicRestartCommand = new MusicRestartCommand();

        //en_US
        commands = new HashMap<String, ICommand>();
        commands.put("play", musicPlayCommand);
        commands.put("p", musicPlayCommand);
        commands.put("stop", musicStopCommand);
        commands.put("skip", musicSkipCommand);
        commands.put("shuffle", musicShuffleCommand);
        commands.put("pause", musicPauseCommand);
        commands.put("unpause", musicUnpauseCommand);
        commands.put("np", musicNpCommand);
        commands.put("nowplaying", musicNpCommand);
        commands.put("repeat", musicRepeatCommand);
        commands.put("queue", musicQueueCommand);
        commands.put("playlist", musicPlaylistCommand);
        commands.put("radio", musicRadioCommand);
        commands.put("volume", musicVolumeCommand);
        commands.put("restart", musicRestartCommand);
        lcommands.put(Localization.en_US, commands);
	}
	
	@Override public Mono<?> execute(Message message, CommandData data, String argument) {
        if (argument.equals(""))
        	return message.getChannel().flatMap(c->c.createMessage(data.localize(LocalizedString.NoSuchSubcommand)));

        String cmd = ArgumentParser.getCommand(argument);
        String arg = ArgumentParser.getArgument(argument);
        
        ICommand command = lcommands.get(data.locale.locale).get(cmd);
        
        if (command==null) return Mono.error(new LocalizedException(LocalizedString.NoSuchSubcommand));
	    
        return command.execute(message, data, arg);
    }

	@Override public HashMap<String, ICommand> getCommands(Localization locale) { return lcommands.get(locale); }
}

@Help(help=LocalizedString.MusicPlaylistCommandHelp, ehelp=LocalizedString.MusicPlaylistCommandExtendedHelp)
class MusicPlaylistCommand implements IGroupCommand {
	private HashMap<Localization, HashMap<String, ICommand>> lcommands;
	public MusicPlaylistCommand() {
		HashMap<String, ICommand> commands;
        lcommands = new HashMap<Localization, HashMap<String, ICommand>>();

        //Commands
        ICommand createCommand = new MusicPlayListCreateCommand();
        ICommand addCommand = new MusicPlayListAddCommand();
        ICommand deleteCommand = new MusicPlayListDeleteCommand();
        ICommand playCommand = new MusicPlayListPlayCommand();
        ICommand removeCommand = new MusicPlayListRemoveCommand();
        ICommand quickAddCommand = new MusicPlayListQuickAddCommand();
        //TODO: List, Info
        
        //en_US
        commands = new HashMap<String, ICommand>();
        commands.put("create", createCommand);
        commands.put("add", addCommand);
        commands.put("delete", deleteCommand);
        commands.put("play", playCommand);
        commands.put("remove", removeCommand);
        commands.put("quickadd", quickAddCommand);
        lcommands.put(Localization.en_US, commands);
	}
	
	@Override public Mono<?> execute(Message message, CommandData data, String argument) {
        if (argument.equals("")) return Mono.error(new LocalizedException(LocalizedString.NoSuchSubcommand));

        String cmd = ArgumentParser.getCommand(argument);
        String arg = ArgumentParser.getArgument(argument);
        
        ICommand command = lcommands.get(data.locale.locale).get(cmd);
        
        if (command==null) return Mono.error(new LocalizedException(LocalizedString.NoSuchSubcommand));
	    
        return command.execute(message, data, arg);
    }

	@Override public HashMap<String, ICommand> getCommands(Localization locale) {
		return lcommands.get(locale);
	}
}

abstract class GenericMusicCommand implements ICommand {
	@Override public Mono<?> execute(Message message, CommandData data, String argument) {
			return message.getChannel()
				.flatMap(channel->BotPermissionUtil.check(channel, new Permission[] {Permission.CONNECT}).then(Mono.just(channel)))
				.flatMap(channel->{
					return message.suppressEmbeds(true)
						.onErrorResume(e->Mono.empty())
						.then(message.getAuthorAsMember())
		            	.flatMap(m->MusicAdapter.getFromMember(data.bot, m))
		            	.flatMap(ma->execute(message, data, argument, ma, channel))
		            	.cast(Object.class)
		            	.onErrorResume(e->{
		            		if (e instanceof VoiceStateMissingException) {
		            			return channel.createMessage(data.localize(LocalizedString.NotInMusicChannel));
		            		}
		            		return Mono.error(e);
		            	});
	        });
			//TODO: We should scan out VoiceStates in other guilds if the current guild does not have a voice state
		}
	
	abstract Mono<?> execute(Message message, CommandData data, String argument, MusicAdapter ma, MessageChannel channel);
	
	public boolean requiresDJ() {return false;}
}

@Help(help=LocalizedString.MusicPlayCommandHelp, ehelp=LocalizedString.MusicPlayCommandExtendedHelp)
class MusicPlayCommand extends GenericMusicCommand {
	@Override public Mono<?> execute(Message message, CommandData data, String argument, MusicAdapter ma, MessageChannel channel) {
		return MusicUtil.lookup(argument).flatMap(track->{
			return ma.queue(track, ma.getQueue().length).flatMap(o->channel.createEmbed(embed->{
				AudioTrackInfo info = track.getInfo();
				embed.setAuthor(data.safe(info.author), info.uri, null);
				embed.setTitle(data.safe(info.title));
                embed.setDescription("Song added to queue (**Length:** "+TimeUtil.formatTime(info.length)+")");
                embed.setFooter("Requested by User ID "+message.getAuthor().get().getId().asLong(), null);
			}));
		}); 
	}
}

@Help(help=LocalizedString.MusicStopCommandHelp, ehelp=LocalizedString.MusicStopCommandExtendedHelp)
class MusicStopCommand extends GenericMusicCommand {
	@Override public Mono<?> execute(Message message, CommandData data, String argument, MusicAdapter ma, MessageChannel channel) {
		ma.stop();
		return channel.createMessage(data.localize(LocalizedString.MusicStopped));
	}
}

@Help(help=LocalizedString.MusicSkipCommandHelp, ehelp=LocalizedString.MusicSkipCommandExtendedHelp)
class MusicSkipCommand extends GenericMusicCommand {
	@Override public Mono<?> execute(Message message, CommandData data, String argument, MusicAdapter ma, MessageChannel channel) {
		if (argument.isEmpty()) {
			ma.skip(0);
		} else try {
			ma.skip(Integer.valueOf(argument));
		} catch (NumberFormatException e) {
			return Mono.error(new LocalizedException(LocalizedString.UnrecognizedUsage));
		}
		return channel.createMessage(data.localize(LocalizedString.TrackSkipped));
	}
}

@Help(help=LocalizedString.MusicShuffleCommandHelp, ehelp=LocalizedString.MusicShuffleCommandExtendedHelp)
class MusicShuffleCommand extends GenericMusicCommand {
	@Override public Mono<?> execute(Message message, CommandData data, String argument, MusicAdapter ma, MessageChannel channel) {
		ma.shuffle();
		return channel.createMessage(data.localize(LocalizedString.QueueShuffled));
	}
}

@Help(help=LocalizedString.MusicPauseCommandHelp, ehelp=LocalizedString.MusicPauseCommandExtendedHelp)
class MusicPauseCommand extends GenericMusicCommand {
	@Override public Mono<?> execute(Message message, CommandData data, String argument, MusicAdapter ma, MessageChannel channel) {
		ma.setPaused(true);
		return channel.createMessage(data.localize(LocalizedString.MusicPaused));
	}
}

@Help(help=LocalizedString.MusicUnpauseCommandHelp, ehelp=LocalizedString.MusicUnpauseCommandExtendedHelp)
class MusicUnpauseCommand extends GenericMusicCommand {
	@Override public Mono<?> execute(Message message, CommandData data, String argument, MusicAdapter ma, MessageChannel channel) {
		ma.setPaused(false);
		return channel.createMessage(data.localize(LocalizedString.MusicUnpaused));
	}
}

@Help(help=LocalizedString.MusicNowPlayingCommandHelp, ehelp=LocalizedString.MusicNowPlayingCommandExtendedHelp)
class MusicNpCommand extends GenericMusicCommand {
	@Override public Mono<?> execute(Message message, CommandData data, String argument, MusicAdapter ma, MessageChannel channel) {
		return showNowPlaying(data, ma, channel);
	}
	
	public static Mono<?> showNowPlaying(CommandData data, MusicAdapter ma, MessageChannel channel) {
		AudioTrack np = ma.getPlaying();
		if (np==null) return channel.createMessage(data.localize(LocalizedString.NoTrackPlaying));
        return channel.createEmbed(embed->{
            AudioTrackInfo info = np.getInfo();
            embed.setColor(Color.BLACK);
            embed.setAuthor(data.safe(info.author), info.uri, null);
            embed.setTitle(data.safe(info.title));
            embed.setDescription("Now playing");
            embed.addField("Length", 
                TimeUtil.formatTime(np.getPosition())+"/"+ TimeUtil.formatTime(info.length)+
                " ("+(Math.floor((((double) np.getPosition())/(double) info.length)*1000.)/10.)+"%)", false);
        });
	}
}

@Help(help=LocalizedString.MusicRepeatCommandHelp, ehelp=LocalizedString.MusicRepeatCommandExtendedHelp)
class MusicRepeatCommand extends GenericMusicCommand {
	@Override public Mono<?> execute(Message message, CommandData data, String argument, MusicAdapter ma, MessageChannel channel) {
		ma.setRepeat(!ma.getRepeat());
		if (ma.getRepeat()) return channel.createMessage(data.localize(LocalizedString.MusicRepeatSet));
		return channel.createMessage(data.localize(LocalizedString.MusicRepeatUnset));
	}
}

@Help(help=LocalizedString.MusicRestartCommandHelp, ehelp=LocalizedString.MusicRestartCommandExtendedHelp)
class MusicRestartCommand extends GenericMusicCommand {
	@Override public Mono<?> execute(Message message, CommandData data, String argument, MusicAdapter ma, MessageChannel channel) {
		AudioTrack playing = ma.getPlaying();
		if (playing==null) return channel.createMessage(data.localize(LocalizedString.NoTrackPlaying));
		playing.setPosition(0);
		return channel.createMessage(data.localize(LocalizedString.MusicRestarted));
	}
}

@Help(help=LocalizedString.MusicQueueCommandHelp, ehelp=LocalizedString.MusicQueueCommandExtendedHelp)
class MusicQueueCommand extends GenericMusicCommand {
	@Override Mono<?> execute(Message message, CommandData data, String argument, MusicAdapter ma, MessageChannel channel) {
		AudioTrack[] queue = ma.getQueue();
		
		if (queue.length==0) return MusicNpCommand.showNowPlaying(data, ma, channel);
		
		return channel.createEmbed(embed->{
			embed.setTitle("Music Queue");
            Long ttime = 0L;
            
            {
            	AudioTrack track = ma.getPlaying();
                embed.addField("Now playing", "**Title:** "+track.getInfo().title+"\n"+
                    "**Length:** "+TimeUtil.formatTime(track.getDuration()), false);
                ttime+=track.getDuration();
            }
            for (int i=0; i<queue.length; i++) {
                AudioTrack track = queue[i];
                embed.addField("Track "+(i+1), "**Title:** "+track.getInfo().title+"\n"+
                    "**Length:** "+TimeUtil.formatTime(track.getDuration()), false);

                ttime+=track.getDuration();
            }
            embed.setFooter("Total length: "+TimeUtil.formatTime(ttime), null);
		});
	}
}

@Help(help=LocalizedString.MusicRadioCommandHelp, ehelp=LocalizedString.MusicRadioCommandExtendedHelp)
class MusicRadioCommand extends GenericMusicCommand {
	@Override public Mono<?> execute(Message message, CommandData data, String argument, MusicAdapter ma, MessageChannel channel) {
		return ma.setRadio(!ma.getRadio()).flatMap(r->{
			if (ma.getRadio()) return channel.createMessage(data.localize(LocalizedString.MusicRadioSet));
			return channel.createMessage(data.localize(LocalizedString.MusicRadioUnset));
		});
	}
}

@Help(help=LocalizedString.MusicPlaylistAddCommandHelp, ehelp=LocalizedString.MusicPlaylistAddCommandExtendedHelp)
class MusicPlayListAddCommand implements ICommand {
	@Override public Mono<?> execute(Message message, CommandData data, String argument) {
		return message.getChannel().flatMap(channel->{
			ArgumentParser parser = new ArgumentParser(argument);
			if (parser.isEmpty()) return Mono.error(new LocalizedException(LocalizedString.UnrecognizedUsage));
			if (!parser.couldBeQuote()) return Mono.error(new LocalizedException(LocalizedString.UseQuotesPlaylist));
			String quote = parser.eatQuote();
			if (parser.isEmpty()) return Mono.error(new LocalizedException(LocalizedString.UnrecognizedUsage));
			return MusicUtil.lookup(parser.toString()).flatMap(track->{
				return UserAdapter.of(data.bot, message.getAuthor().get().getId().asLong()).getDocument().flatMap(doc->{
					DBObject obj = doc.getObject();
					if (!obj.has("playlists")) return Mono.error(new LocalizedException(LocalizedString.CreatePlaylistFirst));
					
					DBObject playlists = obj.getOrDefaultObject("playlists", null);
					if (!playlists.has(quote)) return Mono.error(new LocalizedException(LocalizedString.CreatePlaylistFirst));
					
					DBArray playlist = playlists.getOrDefaultArray(quote, null);
					if (playlist.getLength()>20) return Mono.error(new LocalizedException(LocalizedString.PlaylistTooManySongs));
					
					DBObject item = new DBObject();
					item.set("url", track.getInfo().uri);
					playlist.add(item);
					
					return doc.save().then(channel.createEmbed(embed->{
						//TODO: Already localized in files, just need to set
						AudioTrackInfo info = track.getInfo();
						embed.setAuthor(data.safe(info.author), info.uri, null);
						embed.setTitle(data.safe(info.title));
		                embed.setDescription("Song added to playlist (**Length:** "+TimeUtil.formatTime(info.length)+")");
					}));
				});
			});
		});
	}
}

@Help(help=LocalizedString.MusicPlaylistCreateCommandHelp, ehelp=LocalizedString.MusicPlaylistCreateCommandExtendedHelp)
class MusicPlayListCreateCommand implements ICommand {
	@Override public Mono<?> execute(Message message, CommandData data, String argument) {
		return message.getChannel().flatMap(channel->{
			ArgumentParser parser = new ArgumentParser(argument);
			if (parser.isEmpty()) return Mono.error(new LocalizedException(LocalizedString.UnrecognizedUsage));
			String quote = parser.couldBeQuote()?parser.eatQuote():parser.toString();
			return UserAdapter.of(data.bot, message.getAuthor().get().getId().asLong()).getDocument().flatMap(doc->{
				DBObject obj = doc.getObject();
				
				DBObject playlists = obj.getOrSetObject("playlists", new DBObject());
				
				DBArray playlist = playlists.getOrDefaultArray(quote, null);
				if (playlist!=null) return Mono.error(new LocalizedException(LocalizedString.PlaylistAlreadyExists));
				
				playlists.createArray(quote, arr->{});
				
				return doc.save().then(channel.createMessage(data.safe(data.localize(LocalizedString.PlaylistCreated, FillinUtil.of("name", quote)))));
			});
		});
	}
}

@Help(help=LocalizedString.MusicPlaylistDeleteCommandHelp, ehelp=LocalizedString.MusicPlaylistDeleteCommandExtendedHelp)
class MusicPlayListDeleteCommand implements ICommand {
	@Override public Mono<?> execute(Message message, CommandData data, String argument) {
		return message.getChannel().flatMap(channel->{
			ArgumentParser parser = new ArgumentParser(argument);
			if (parser.isEmpty()) return Mono.error(new LocalizedException(LocalizedString.UnrecognizedUsage));
			String quote = parser.couldBeQuote()?parser.eatQuote():parser.toString();
			return UserAdapter.of(data.bot, message.getAuthor().get().getId().asLong()).getDocument().flatMap(doc->{
				DBObject obj = doc.getObject();
				
				DBObject playlists = obj.getOrDefaultObject("playlists", new DBObject());
				if (!playlists.has(quote)) return Mono.error(new LocalizedException(LocalizedString.PlaylistDoesNotExist));
				
				playlists.remove(quote);
				
				return doc.save().then(channel.createMessage(data.safe(LocalizedString.PlaylistDeleted)));
			});
		});
	}
}

@Help(help=LocalizedString.MusicPlaylistPlayCommandHelp, ehelp=LocalizedString.MusicPlaylistPlayCommandExtendedHelp)
class MusicPlayListPlayCommand extends GenericMusicCommand {
	@Override public Mono<?> execute(Message message, CommandData data, String argument, MusicAdapter ma, MessageChannel channel) {
		ArgumentParser parser = new ArgumentParser(argument);
		if (parser.isEmpty()) return Mono.error(new LocalizedException(LocalizedString.UnrecognizedUsage));
		if (!parser.couldBeQuote()) return Mono.error(new LocalizedException(LocalizedString.UseQuotesPlaylist));
		String quote = parser.eatQuote();
		return UserAdapter.of(data.bot, message.getAuthor().get().getId().asLong()).getDocument().flatMap(doc->{
			DBObject obj = doc.getObject();
			if (!obj.has("playlists")) return Mono.error(new LocalizedException(LocalizedString.CreatePlaylistFirst));
			
			DBObject playlists = obj.getOrDefaultObject("playlists", null);
			if (!playlists.has(quote)) return Mono.error(new LocalizedException(LocalizedString.CreatePlaylistFirst));
			
			DBArray playlist = playlists.getOrDefaultArray(quote, null);
			String[] urls = new String[playlist.getLength()];
			for (int i=0; i<urls.length; i++)
				urls[i] = playlist.getObject(i).getOrDefaultString("url", null);
			
			return Flux.fromArray(urls)
				.flatMap(url->MusicUtil.lookup(url))
				.flatMap(track->ma.queue(track, ma.getQueue().length))
			.then(channel.createMessage(data.localize(LocalizedString.PlaylistQueued))); //TODO: User ID
		});
	}
}

@Help(help=LocalizedString.MusicPlaylistRemoveCommandHelp, ehelp=LocalizedString.MusicPlaylistRemoveCommandExtendedHelp)
class MusicPlayListRemoveCommand implements ICommand {
	@Override public Mono<?> execute(Message message, CommandData data, String argument) {
		return message.getChannel().flatMap(channel->{
			ArgumentParser parser = new ArgumentParser(argument);
			if (parser.isEmpty()) return Mono.error(new LocalizedException(LocalizedString.UnrecognizedUsage));
			if (!parser.couldBeQuote()) return Mono.error(new LocalizedException(LocalizedString.UseQuotesPlaylist));
			String quote = parser.eatQuote();
			if (!parser.isNumerical()) return Mono.error(new LocalizedException(LocalizedString.UnrecognizedUsage));
			int i = (int) parser.eatNumerical();
			
			return UserAdapter.of(data.bot, message.getAuthor().get().getId().asLong()).getDocument().flatMap(doc->{
				DBObject obj = doc.getObject();
				if (!obj.has("playlists")) return Mono.error(new LocalizedException(LocalizedString.CreatePlaylistFirst));
				
				DBObject playlists = obj.getOrDefaultObject("playlists", null);
				if (!playlists.has(quote)) return Mono.error(new LocalizedException(LocalizedString.CreatePlaylistFirst));
				
				DBArray playlist = playlists.getOrDefaultArray(quote, null);
				
				playlist.remove(i);
				
				
				return doc.save().then(channel.createMessage(data.safe(LocalizedString.PlaylistTrackRemoved)));
			});
		});
	}
}

@Help(help=LocalizedString.MusicPlaylistQuickAddCommandHelp, ehelp=LocalizedString.MusicPlaylistQuickAddCommandExtendedHelp)
class MusicPlayListQuickAddCommand extends GenericMusicCommand {
	@Override public Mono<?> execute(Message message, CommandData data, String argument, MusicAdapter ma, MessageChannel channel) {
		ArgumentParser parser = new ArgumentParser(argument);
		if (parser.isEmpty()) return Mono.error(new LocalizedException(LocalizedString.UnrecognizedUsage));
		if (!parser.couldBeQuote()) return Mono.error(new LocalizedException(LocalizedString.UseQuotesPlaylist));
		String quote = parser.eatQuote();
		return UserAdapter.of(data.bot, message.getAuthor().get().getId().asLong()).getDocument().flatMap(doc->{
			AudioTrack track = ma.getPlaying();
			if (track==null) return Mono.error(new LocalizedException(LocalizedString.NoTrackPlaying));
			
			DBObject obj = doc.getObject();
			if (!obj.has("playlists")) return Mono.error(new LocalizedException(LocalizedString.CreatePlaylistFirst));
			
			DBObject playlists = obj.getOrDefaultObject("playlists", null);
			if (!playlists.has(quote)) return Mono.error(new LocalizedException(LocalizedString.CreatePlaylistFirst));
			
			DBArray playlist = playlists.getOrDefaultArray(quote, null);
			if (playlist.getLength()>20) return Mono.error(new LocalizedException(LocalizedString.PlaylistTooManySongs));
			
			DBObject item = new DBObject();
			item.set("url", track.getInfo().uri);
			playlist.add(item);
			
			return doc.save().then(channel.createMessage(data.localize(LocalizedString.PlaylistTrackAdded)));
		});
	}
}

@Help(help=LocalizedString.MusicVolumeCommandHelp, ehelp=LocalizedString.MusicVolumeCommandExtendedHelp)
class MusicVolumeCommand implements IGroupCommand {
	private HashMap<Localization, HashMap<String, ICommand>> lcommands;
	public MusicVolumeCommand() {
		HashMap<String, ICommand> commands;
        lcommands = new HashMap<Localization, HashMap<String, ICommand>>();

        //Commands
        ICommand upCommand = new MusicVolumeUpCommand();
        ICommand downCommand = new MusicVolumeDownCommand();
        ICommand setCommand = new MusicVolumeSetCommand();
        ICommand checkCommand = new MusicVolumeCheckCommand();
        
        //en_US
        commands = new HashMap<String, ICommand>();
        commands.put("up", upCommand);
        commands.put("down", downCommand);
        commands.put("set", setCommand);
        commands.put("check", checkCommand);
        
        lcommands.put(Localization.en_US, commands);
	}
	
	@Override public Mono<?> execute(Message message, CommandData data, String argument) {
        if (argument.equals("")) return Mono.error(new LocalizedException(LocalizedString.NoSuchSubcommand));

        String cmd = ArgumentParser.getCommand(argument);
        String arg = ArgumentParser.getArgument(argument);
        
        ICommand command = lcommands.get(data.locale.locale).get(cmd);
        
        if (command==null) return Mono.error(new LocalizedException(LocalizedString.NoSuchSubcommand));
	    
        return command.execute(message, data, arg);
    }

	@Override public HashMap<String, ICommand> getCommands(Localization locale) {
		return lcommands.get(locale);
	}
}

@Help(help=LocalizedString.MusicVolumeUpCommandHelp, ehelp=LocalizedString.MusicVolumeUpCommandExtendedHelp)
class MusicVolumeUpCommand extends GenericMusicCommand {
	@Override Mono<?> execute(Message message, CommandData data, String argument, MusicAdapter ma, MessageChannel channel) {
		ArgumentParser parser = new ArgumentParser(argument);
		if (!parser.isNumerical()) return Mono.error(new LocalizedException(LocalizedString.UnrecognizedUsage));
		int nv = ma.getVolume()+(int) parser.eatNumerical();
		if (nv<0) return Mono.error(new LocalizedException(LocalizedString.VolumeTooLow));
		ma.setVolume(nv);
		return channel.createMessage(data.localize(LocalizedString.VolumeSet, FillinUtil.of("volume", String.valueOf(ma.getVolume()))));
	}
}

@Help(help=LocalizedString.MusicVolumeDownCommandHelp, ehelp=LocalizedString.MusicVolumeDownCommandExtendedHelp)
class MusicVolumeDownCommand extends GenericMusicCommand {
	@Override Mono<?> execute(Message message, CommandData data, String argument, MusicAdapter ma, MessageChannel channel) {
		ArgumentParser parser = new ArgumentParser(argument);
		if (!parser.isNumerical()) return Mono.error(new LocalizedException(LocalizedString.UnrecognizedUsage));
		int nv = ma.getVolume()-(int) parser.eatNumerical();
		if (nv<0) return Mono.error(new LocalizedException(LocalizedString.VolumeTooLow));
		ma.setVolume(nv);
		return channel.createMessage(data.localize(LocalizedString.VolumeSet, FillinUtil.of("volume", String.valueOf(ma.getVolume()))));
	}
}

@Help(help=LocalizedString.MusicVolumeSetCommandHelp, ehelp=LocalizedString.MusicVolumeSetCommandExtendedHelp)
class MusicVolumeSetCommand extends GenericMusicCommand {
	@Override Mono<?> execute(Message message, CommandData data, String argument, MusicAdapter ma, MessageChannel channel) {
		ArgumentParser parser = new ArgumentParser(argument);
		if (!parser.isNumerical()) return Mono.error(new LocalizedException(LocalizedString.UnrecognizedUsage));
		int nv = (int) parser.eatNumerical();
		if (nv<0) return Mono.error(new LocalizedException(LocalizedString.VolumeTooLow, FillinUtil.of("volume", String.valueOf(ma.getVolume()))));
		ma.setVolume(nv);
		return channel.createMessage(data.localize(LocalizedString.VolumeSet, FillinUtil.of("volume", String.valueOf(ma.getVolume()))));
	}
}

@Help(help=LocalizedString.MusicVolumeCheckCommandHelp, ehelp=LocalizedString.MusicVolumeCheckCommandExtendedHelp)
class MusicVolumeCheckCommand extends GenericMusicCommand {
	@Override Mono<?> execute(Message message, CommandData data, String argument, MusicAdapter ma, MessageChannel channel) {
		return channel.createMessage(data.localize(LocalizedString.CurrentVolume, FillinUtil.of("volume", String.valueOf(ma.getVolume()))));
	}
}

//search, set time, trim