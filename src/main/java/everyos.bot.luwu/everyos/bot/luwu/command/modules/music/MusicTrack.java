package everyos.bot.luwu.command.modules.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

public class MusicTrack {
	public AudioTrack getAudioPart() {
		return null;
	}

	public long getTrimLeft() {
		return 0;
	}

	public long getTrimRight() {
		return getAudioPart().getDuration();
	}
}
