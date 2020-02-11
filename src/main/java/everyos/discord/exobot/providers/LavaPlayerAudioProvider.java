package everyos.discord.exobot.providers;

import java.nio.ByteBuffer;

import com.sedmelluq.discord.lavaplayer.format.StandardAudioDataFormats;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.playback.MutableAudioFrame;

import discord4j.voice.AudioProvider;

public final class LavaPlayerAudioProvider extends AudioProvider {
    private AudioPlayer player;
    private final MutableAudioFrame frame = new MutableAudioFrame();

    public LavaPlayerAudioProvider() {
        super(ByteBuffer.allocate(StandardAudioDataFormats.DISCORD_OPUS.maximumChunkSize()));
        frame.setBuffer(getBuffer());
    }

    @Override public boolean provide() {
        final boolean didProvide = player.provide(frame);
        if (didProvide) getBuffer().flip();
        return didProvide;
    }

    public void setPlayer(AudioPlayer player) {
        this.player = player;
    }
}