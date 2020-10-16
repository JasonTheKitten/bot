package everyos.bot.luwu.run.command.modules.music;

import java.nio.ByteBuffer;

import com.sedmelluq.discord.lavaplayer.format.StandardAudioDataFormats;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.playback.MutableAudioFrame;

import everyos.bot.chat4j.audio.AudioBridge;

public class LavaPlayerAudioBridge extends AudioBridge {
	private AudioPlayer player;
	private final MutableAudioFrame frame = new MutableAudioFrame();

	public LavaPlayerAudioBridge(AudioPlayer player) {
		//TODO: Perhaps this should be moved to a .createBuffer, with arguments to support other formats
		super(ByteBuffer.allocate(StandardAudioDataFormats.DISCORD_OPUS.maximumChunkSize()));
		frame.setBuffer(getBuffer());
		this.player = player;
	}

	@Override public boolean provide() {
		final boolean didProvide = player.provide(frame);
		if (didProvide) getBuffer().flip();
		return didProvide;
	}
}
