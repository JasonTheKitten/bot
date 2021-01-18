package everyos.bot.luwu.run.command.modules.music;

import java.util.ArrayList;
import java.util.List;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.playback.NonAllocatingAudioFrameBuffer;

import everyos.bot.chat4j.audio.AudioBridge;
import everyos.bot.luwu.run.command.modules.music.MusicCache.MusicCacheFinalizer;

public class MusicManager {
	private static AudioPlayerManager manager;
	
	private final MusicTrackScheduler scheduler;
	private final MusicCacheFinalizer finalizer;
	private final AudioPlayer player;
	private final AudioBridge bridge;
	
	private final List<Runnable> cleanupListeners = new ArrayList<>();
	private final MusicQueue queue = new MusicQueue();
	private MusicTrack playing = null;
	private AudioTrack audio = null;
	private boolean repeat = false;
	private boolean radio = false;
	
	static {
		manager = new DefaultAudioPlayerManager();
        manager.getConfiguration().setFrameBufferFactory(NonAllocatingAudioFrameBuffer::new);
        AudioSourceManagers.registerRemoteSources(manager);
	}
	
	public MusicManager(MusicCacheFinalizer finalizer) {
		this.finalizer = finalizer;
		
		player = manager.createPlayer();
		bridge = new LavaPlayerAudioBridge(player);
		
		this.scheduler = new MusicTrackScheduler(player, this);
		player.addListener(scheduler);
	}
	
	public void addCleanupListener(Runnable r) {
		cleanupListeners.add(r);
	}
	
	public MusicQueue getQueue() {
		return queue;
	}
	
	public void setRepeat(boolean repeat) {
		this.repeat = repeat;
	}
	public boolean getRepeat() {
		return repeat;
	}
	
	public void setRadio(boolean radio) {
		this.radio = radio;
	}
	public boolean getRadio() {
		return radio;
	}
	
	public void setVolume(int volume) {
		player.setVolume(volume);
	}
	public int getVolume() {
		return player.getVolume();
	}
	
	public AudioBridge getBridge() {
		return bridge;
	}

	public void stop() {
		for (Runnable r: cleanupListeners ) {
			r.run();
		}
		player.destroy();
		finalizer.cleanup();
	}

	public MusicTrack getPlaying() {
		return this.playing;
	}
	public AudioTrack getPlayingAudio() {
		return this.audio;
	}

	public void pause() {
		player.setPaused(true);
	}
	public void unpause() {
		player.setPaused(false);
	}

	//TODO: None of the below should exist
	protected void setPlaying(MusicTrack track, AudioTrack atrack) {
		this.playing = track;
		this.audio = atrack;
	}
	
	public void ready() {
		if (this.playing==null) {
			scheduler.playNext();
		}
	}
	
	public void playNext() {
		scheduler.playNext();
	}
}
