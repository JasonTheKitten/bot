package everyos.bot.luwu.command.modules.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import com.sedmelluq.discord.lavaplayer.track.TrackMarker;
import com.sedmelluq.discord.lavaplayer.track.TrackMarkerHandler;

public class MusicTrackScheduler extends AudioEventAdapter {
	private AudioPlayer player;
	private MusicManager manager;

	public MusicTrackScheduler(AudioPlayer player, MusicManager manager) {
		this.player = player;
		this.manager = manager;
	}

	@Override public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
		if (!endReason.mayStartNext) return;
		playNext();
	}
	@Override public void onTrackStuck(AudioPlayer player, AudioTrack track, long thresholdMs) {
		playNext();
	}
	@Override public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception) {
		//TODO: Indicate an error has occured?
		exception.printStackTrace();
		playNext();
	}
	
	private void playNext() {
		MusicQueue queue = manager.getQueue();
		if (manager.getRepeat()) {
			queue.queue(manager.getPlaying());
		} else if (manager.getRadio()) {
			//Radio code
		}
		if (queue.size()>0) {
			MusicTrack track = queue.pop();
			manager.setPlaying(track);
			playTrack(track);
		} else {
			stop();
		}
	}
	private void playTrack(MusicTrack mtrack) {
		AudioTrack track = mtrack.getAudioPart().makeClone();
		track.setPosition(mtrack.getTrimLeft());
		track.setMarker(new TrackMarker(mtrack.getTrimRight(), state->{
			playNext();
		}));
		player.playTrack(track);
	}
	
	private void stop() {
		manager.stop();
	}
}
