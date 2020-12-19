package everyos.bot.luwu.run.command.modules.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

public class MusicTrack {
	private AudioTrack track;

	public MusicTrack(AudioTrack track) {
		this.track = track;
	}

	public AudioTrack getAudioPart() {
		return track;
	}

	public long getTrimLeft() {
		return 0;
	}

	public long getTrimRight() {
		return getAudioPart().getDuration();
	}
}
