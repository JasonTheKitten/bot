package everyos.bot.luwu.run.command.modules.music;

import java.util.Optional;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import everyos.bot.luwu.core.entity.UserID;

public class MusicTrack {
	private AudioTrack track;
	private Optional<UserID> invoker;

	public MusicTrack(AudioTrack track, UserID invoker) {
		this.track = track;
		this.invoker = Optional.ofNullable(invoker);
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

	public Optional<UserID> getQueuedBy() {
		return invoker;
	}
}
