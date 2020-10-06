package everyos.bot.luwu.command.modules.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.track.playback.NonAllocatingAudioFrameBuffer;

import everyos.bot.chat4j.audio.AudioBridge;
import everyos.bot.luwu.command.modules.music.MusicCache.MusicCacheFinalizer;

public class MusicManager {
	private static AudioPlayerManager manager;
	
	private MusicCacheFinalizer finalizer;
	private AudioPlayer player;
	private AudioBridge bridge;
	
	private MusicQueue queue;
	private MusicTrack playing;
	private boolean repeat;
	private boolean radio;
	
	static {
		manager = new DefaultAudioPlayerManager();
        manager.getConfiguration().setFrameBufferFactory(NonAllocatingAudioFrameBuffer::new);
        AudioSourceManagers.registerRemoteSources(manager);
	}
	
	public MusicManager(MusicCacheFinalizer finalizer) {
		this.finalizer = finalizer;
		
		player = manager.createPlayer();
		bridge = new LavaPlayerAudioBridge(player);
		
		player.addListener(new MusicTrackScheduler(player, this));
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
		player.destroy();
		finalizer.cleanup();
	}

	public MusicTrack getPlaying() {
		return this.playing;
	}
	protected void setPlaying(MusicTrack track) { //TODO: This should not exist
		this.playing = track;
	}

	public void pause() {
		
	}
	public void unpause() {
		
	}
}
