package everyos.discord.exobot.objects;

import java.util.function.Consumer;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;

import discord4j.core.object.entity.VoiceChannel;
import discord4j.core.spec.VoiceChannelJoinSpec;
import discord4j.voice.AudioProvider;
import discord4j.voice.VoiceConnection;
import reactor.core.publisher.Mono;

public class VoiceChannelObject {
    public ChannelObject generic;
    public VoiceChannel channel;
    public AudioPlayer player;
    public AudioProvider provider;
    
    private MusicObject musicObject;

    public VoiceChannelObject(ChannelObject generic) {
        this.generic = generic;
        this.channel = (VoiceChannel) generic.channel;
    }

    public Mono<? super VoiceConnection> join(Consumer<? super VoiceChannelJoinSpec> func) {
        return this.channel.join(func);
    }
    
    //Music
    public MusicObject getMusicObject(){
    	if (musicObject==null) musicObject = new MusicObject(this);
    	return musicObject;
    }
};