package everyos.bot.luwu.util;

import java.net.URL;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.playback.NonAllocatingAudioFrameBuffer;

import everyos.bot.luwu.run.api.Google;
import reactor.core.publisher.Mono;

public class MusicUtil {
	private static AudioPlayerManager loader;
	static {
		loader = new DefaultAudioPlayerManager();
        loader.getConfiguration().setFrameBufferFactory(NonAllocatingAudioFrameBuffer::new);
        AudioSourceManagers.registerRemoteSources(loader);
	}
	
	public static Mono<AudioTrack> lookup(String trackname) {
		String trackURL;
		try {
			new URL(trackname);
			trackURL = trackname;
		} catch (Exception e) {
			try {
				trackURL = "https://youtube.com/watch?v="+Google.search(trackname).getItems().get(0).getId().getVideoId();
			} catch (Exception e2) { return Mono.error(e2); }
		}
		
		String ftrackurl = trackURL;
		
		return Mono.create(sink->{
			loader.loadItem(ftrackurl, new AudioLoadResultHandler() {
				@Override public void trackLoaded(AudioTrack track) {
					sink.success(track);
				}

				@Override public void playlistLoaded(AudioPlaylist playlist) {
					//We don't support playlists
					sink.error(new Exception());
				}

				@Override public void noMatches() {
					sink.error(new MusicUtil.NoSuchTrackException());
				}

				@Override public void loadFailed(FriendlyException exception) {
					sink.error(exception);
				}
			});
		});
		
	}
	
	public static class NoSuchTrackException extends Exception {
		private static final long serialVersionUID = 4803294008980570417L;
	}
}

