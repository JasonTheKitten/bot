package everyos.discord.exobot.objects;

import java.util.LinkedList;
import java.util.function.Consumer;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

import everyos.discord.exobot.Statics;
import everyos.discord.exobot.providers.LavaPlayerAudioProvider;

public class MusicObject {
    public VoiceChannelObject generic;
    
    private AudioPlayer player;
    private TrackScheduler scheduler;
    private LavaPlayerAudioProvider provider;
    
    public MusicObject(VoiceChannelObject generic) {
        this.generic = generic;
        
        this.provider = new LavaPlayerAudioProvider();
    	
    	player = Statics.playerManager.createPlayer();
    	scheduler = new TrackScheduler(player);
        player.addListener(scheduler);
        
        Statics.musicChannels.add(this);

        join();
    }

    public void join() {
        generic.join(handle->{
            provider.setPlayer(player);
            handle.setProvider(provider);
        }).subscribe();
    }
    
    public void play(String url, Consumer<? super AudioTrack> onLoaded, Runnable onError) {
    	 Statics.playerManager.loadItem(url, new TrackLoader(scheduler, track->{
             onLoaded.accept(track);
         }, onError));
    }

	public void repeat(boolean b) {
		synchronized(scheduler) {
			scheduler.repeat = b;
		}
	}
	
	public void skip() {
        synchronized(scheduler) {
            scheduler.skip();
        }
	}
	
	public void stop() {
        synchronized(scheduler) {
            scheduler.stop();
        }
    }
    
    public void setPaused(boolean paused) {
        scheduler.setPaused(paused); //Does this need synchronized?
    }

    public void shuffle() {
        synchronized(scheduler) {
            scheduler.shuffle();
        }
    }

    public LinkedList<AudioTrack> getQueue() {
        synchronized(scheduler) {
            return scheduler.playlist;
        }
    }
}

final class TrackLoader implements AudioLoadResultHandler {
    private Consumer<? super AudioTrack> onLoaded;
	private TrackScheduler scheduler;
    private Runnable onError;
	
    public TrackLoader(TrackScheduler scheduler, Consumer<? super AudioTrack> onLoaded, Runnable onError) {
        this.onLoaded = onLoaded;
        this.onError = onError;
        this.scheduler = scheduler;
    }
    @Override public void trackLoaded(final AudioTrack track) {
    	scheduler.queue(track);
        onLoaded.accept(track);
    }
    @Override public void playlistLoaded(final AudioPlaylist playlist) {
        playlist.getTracks().forEach(track->{
            scheduler.queue(track);
            onLoaded.accept(track);
        });
    }
    @Override public void noMatches() {onError.run();}
    @Override public void loadFailed(final FriendlyException exception) {onError.run();}
}

final class TrackScheduler extends AudioEventAdapter {
	private AudioPlayer player;
	public LinkedList<AudioTrack> playlist;
	
    public boolean repeat;

	public TrackScheduler(AudioPlayer player) {
		this.player = player;
		this.playlist = new LinkedList<AudioTrack>();
	}

    public void queue(AudioTrack track) {
		playlist.add(track);
		if (playlist.size()==1) player.playTrack(track);
	}
	
	@Override public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
		if (!endReason.mayStartNext) return;
		if (repeat) playlist.add(playlist.get(0).makeClone());
		playlist.remove(0);
		AudioTrack ntrack = playlist.get(0);
		if (ntrack == null) return;
		player.playTrack(ntrack);
    }
    
    @Override public void onTrackStuck(AudioPlayer player, AudioTrack track, long thresholdMs) {
        playlist.remove(0);
        AudioTrack newTrack = track.makeClone();
        playlist.addFirst(track);
        newTrack.setPosition(track.getPosition());
        player.playTrack(newTrack);
    }
	
	public void skip() {
		playlist.remove(0);
		player.stopTrack();
		AudioTrack ntrack = playlist.get(0);
		if (ntrack == null) return;
		player.playTrack(ntrack);
	}
	
	public void stop() {
		playlist.clear();
		player.stopTrack();
    }
    
    public void shuffle() {
        LinkedList<AudioTrack> newlist = new LinkedList<AudioTrack>();
        newlist.add(playlist.get(0));
        playlist.remove(0);
        while (playlist.size()>0) {
            int rand = (int) Math.round(Math.random()*(playlist.size()-1));
            newlist.add(playlist.get(rand));
            playlist.remove(rand);
        }
        playlist = newlist;
    }

    public void setPaused(boolean paused) {
        player.setPaused(paused);
    }
}