package everyos.bot.luwu.run.command.modules.music;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.TrackMarker;

public class MusicTrackScheduler extends AudioEventAdapter {
	private static int maxSongFrequency = 30; 
	
	private static final String[] radioSongs;
	static {
		InputStream in = ClassLoader.getSystemResourceAsStream("radio.txt");
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        List<String> radio = new ArrayList<String>();
        reader.lines().forEach(line->radio.add(line));
        try {
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
        radioSongs = radio.toArray(new String[radio.size()]);
	}
	
	
	
	private AudioPlayer player;
	private MusicManager manager;
	
	private final int radioIntervalWidth;
	private int radioIntervalIndex;

	public MusicTrackScheduler(AudioPlayer player, MusicManager manager) {
		this.player = player;
		this.manager = manager;
		
		radioIntervalWidth = radioSongs.length/maxSongFrequency;
		radioIntervalIndex = (int)(Math.random()*radioSongs.length);
	}

	@Override
	public void onTrackStuck(AudioPlayer player, AudioTrack track, long thresholdMs) {
		playNext();
	}
	
	@Override
	public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception) {
		//TODO: Indicate an error has occured?
		System.out.println("Problem in track "+track.getIdentifier());
		exception.printStackTrace();
		playNext();
	}
	
	public void playNext() {
		MusicQueue queue = manager.getQueue();
		if (manager.getRepeat()&&manager.getPlaying()!=null) {
			queue.queue(manager.getPlaying());
		} else if (manager.getRadio()&&queue.size()==0) {
			//Radio code
			int selectedSong = (radioIntervalIndex+(int)(Math.random()*radioIntervalWidth))%radioSongs.length;
			
			MusicUtil.lookup(radioSongs[selectedSong]).doOnNext(atrack->{
				MusicTrack track = new MusicTrack(atrack);
				playTrack(track);
			}).subscribe();
			
			radioIntervalIndex = (selectedSong+1)%radioSongs.length;
			
			return;
		}
		if (queue.size()>0) {
			MusicTrack track = queue.pop();
			playTrack(track);
		} else {
			stop();
		}
	}
	private void playTrack(MusicTrack mtrack) {
		AudioTrack track = mtrack.getAudioPart().makeClone();
		manager.setPlaying(mtrack, track);
		track.setPosition(mtrack.getTrimLeft());
		track.setMarker(new TrackMarker(mtrack.getTrimRight(), state->{
			if (manager.getPlayingAudio()==track) {
				playNext();
			}
		}));
		player.playTrack(track);
	}
	
	private void stop() {
		manager.stop();
	}
}
