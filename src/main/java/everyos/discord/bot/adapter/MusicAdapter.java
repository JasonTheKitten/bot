package everyos.discord.bot.adapter;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import com.sedmelluq.discord.lavaplayer.format.StandardAudioDataFormats;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import com.sedmelluq.discord.lavaplayer.track.playback.MutableAudioFrame;
import com.sedmelluq.discord.lavaplayer.track.playback.NonAllocatingAudioFrameBuffer;

import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.channel.VoiceChannel;
import discord4j.rest.util.Snowflake;
import discord4j.voice.AudioProvider;
import discord4j.voice.VoiceConnection;
import everyos.discord.bot.BotInstance;
import everyos.discord.bot.database.DBDocument;
import everyos.discord.bot.localization.LocalizedString;
import everyos.discord.bot.util.ErrorUtil.LocalizedException;
import everyos.discord.bot.util.MusicUtil;
import reactor.core.publisher.Mono;

public class MusicAdapter implements IAdapter {
	public static class VoiceStateMissingException extends Exception {
		private static final long serialVersionUID = -8308486187759343621L;	
	}
	
	private static HashMap<Long, MusicAdapter> adapters;
	private static AudioPlayerManager manager;
	static {
		adapters = new HashMap<Long, MusicAdapter>();
		
		manager = new DefaultAudioPlayerManager();
        manager.getConfiguration().setFrameBufferFactory(NonAllocatingAudioFrameBuffer::new);
        AudioSourceManagers.registerRemoteSources(manager);
	}
	
	private BotInstance instance;
    
    private VoiceChannel channel;
    private TrackScheduler scheduler;
    private AudioPlayer player;
    private AudioProvider provider;
	private long channelID;

    public MusicAdapter(BotInstance instance) {
    	this.instance = instance;
        
        player = manager.createPlayer();
    	provider = new LavaPlayerAudioProvider(player);
    	scheduler = new TrackScheduler(player);
    	player.addListener(scheduler);
    }

    public static MusicAdapter of(BotInstance instance, long l) {
    	if (adapters.containsKey(l)) return adapters.get(l);
        MusicAdapter adapter = new MusicAdapter(instance);
        adapters.put(l, adapter);
        return adapter;
    }
    
	public static Mono<MusicAdapter> getFromMember(BotInstance instance, Member member) {
    	return member.getVoiceState()
    		.switchIfEmpty(Mono.error(new VoiceStateMissingException()))
    		.flatMap(vs->{
				if (!(vs.getChannelId().isPresent())) return Mono.error(new LocalizedException(LocalizedString.MustBeInVoiceChannel));
				MusicAdapter adapter = MusicAdapter.of(instance, member.getGuildId().asLong());
				adapter.setPreferredChannel(vs.getChannelId().get().asLong());
				return Mono.just(adapter);
	    	});
    }

    private void setPreferredChannel(long id) {
		this.channelID = id;
	}

	@Override public Mono<DBDocument> getDocument() { return null; }
    
    public AudioPlayer getPlayer() {
    	return player;
    }
    public AudioProvider getProvider() {
    	return provider;
    }
    
    private Mono<VoiceConnection> connect() {
    	Mono<VoiceConnection> vcm;
		if (scheduler.connection == null) {
			Mono<VoiceChannel> mono = channel==null?instance.client.getChannelById(Snowflake.of(channelID)).cast(VoiceChannel.class):Mono.just(channel);
			vcm = mono.flatMap(channel->channel.join(spec->spec.setProvider(getProvider())));
		} else vcm = Mono.just(scheduler.connection);
		return vcm.doOnNext(vc->scheduler.setConnection(vc));
    }

	public Mono<VoiceConnection> queue(AudioTrack track, int i) {
		synchronized(scheduler) {
			return connect().doOnNext(vc->scheduler.queue(track, i));
		}
	}
	
	public void stop() {
		synchronized(scheduler) {scheduler.stop();}
	}
	public void skip(int pos) {
		synchronized(scheduler) {scheduler.skip(pos);}
	}
	public void shuffle() {
		synchronized(scheduler) {scheduler.shuffle();}
	}
	public void setPaused(boolean paused) {
		synchronized(scheduler) {scheduler.setPaused(paused);}
	}
	public AudioTrack getPlaying() {
		/*return player.getPlayingTrack();*/
		synchronized(scheduler) {return scheduler.getPlaying();}
	}

	public boolean getRepeat() {
		synchronized(scheduler) {return scheduler.getRepeat();}
	}
	public void setRepeat(boolean r) {
		synchronized(scheduler) {scheduler.setRepeat(r);}
	}

	public AudioTrack[] getQueue() {
		synchronized(scheduler) { return scheduler.getQueue();}
	}

	public boolean getRadio() {
		synchronized(scheduler) {return scheduler.getRadio();}
	}
	public Mono<VoiceConnection> setRadio(boolean r) {
		synchronized(scheduler) {
			return connect().doOnNext(vc->scheduler.setRadio(r));
		}
	}
}

class TrackScheduler extends AudioEventAdapter {
	public VoiceConnection connection;
	
	private AudioPlayer player;
	private LinkedList<AudioTrack> queue;
	private boolean repeat;
	private AudioTrack playingTrack;
	private boolean playingRadio;
	private String[] radio;

	public TrackScheduler(AudioPlayer player) {
		this.player = player;
		this.queue = new LinkedList<AudioTrack>();
		
		try {
            InputStream in = ClassLoader.getSystemResourceAsStream("radio.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            ArrayList<String> radio = new ArrayList<String>();
            reader.lines().forEach(line->radio.add(line));
            reader.close();
            this.radio = radio.toArray(new String[radio.size()]);
        } catch (Exception e) { e.printStackTrace(); }
	}

	public AudioTrack[] getQueue() {
		return this.queue.toArray(new AudioTrack[this.queue.size()]);
	}

	public void setConnection(VoiceConnection vc) {
		this.connection = vc;
	}

	public void queue(AudioTrack track, int i) {
		queue.add(i, track);
		if (queue.size()==1&&playingTrack==null) playNext();
	}

	@Override public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
		if (!endReason.mayStartNext) return;
		if (repeat) queue.add(playingTrack.makeClone());
		playNext();
	}
	
	public void playNext() {
		synchronized(this) {
			if (queue.isEmpty()&&playingRadio) {
				playingTrack = null;
				
				MusicUtil.lookup(player, radio[(int) Math.round(Math.random()*(radio.length-1))]).doOnNext(track->{
					queue.add(track);
					if (playingTrack==null) playNext();
				}).subscribe();
				return;
			}
			
			if (queue.isEmpty()) {
				stop();
				return;
			}
			
			playingTrack = queue.get(0);
			queue.remove(0);
			player.playTrack(playingTrack);
		}
	}
	
	public void leave() {
		playingTrack = null;
		if (connection!=null) connection.disconnect().subscribe();
		connection = null;
	}

	@Override public void onTrackStuck(AudioPlayer player, AudioTrack track, long thresholdMs) {
		synchronized(this) {
		    playingTrack = playingTrack.makeClone();
		    playingTrack.setPosition(track.getPosition());
		    player.playTrack(playingTrack);
		}
	}

	public void skip(int pos) {
		if (pos==0) {
			player.stopTrack();
			playNext();
		} else try {
			queue.remove(pos-1);
		} catch(Exception e) {};
	}

	public void stop() {
		queue.clear();
		setRepeat(false);
		setRadio(false);
		player.stopTrack();
		leave();
	}

	public void shuffle() {
	    LinkedList<AudioTrack> newlist = new LinkedList<AudioTrack>();
	    while (queue.size()>0) {
	        int rand = (int) Math.round(Math.random()*(queue.size()-1));
	        newlist.add(queue.get(rand));
	        queue.remove(rand);
	    }
	    queue = newlist;
	}

	public void setPaused(boolean paused) {
	    player.setPaused(paused);
	}
	
	public AudioTrack getPlaying() {
		return playingTrack;
	}
	
	public boolean getRepeat() {
		return repeat;
	}
	public void setRepeat(boolean r) {
		repeat = r;
	}
	
	public boolean getRadio() {
		return playingRadio;
	}
	public void setRadio(boolean r) {
		playingRadio = r;
		if (r) repeat = false;
		if (r&&queue.size()==0&&playingTrack==null) playNext();
	}
}

class LavaPlayerAudioProvider extends AudioProvider {
	private AudioPlayer player;
	private final MutableAudioFrame frame = new MutableAudioFrame();

	public LavaPlayerAudioProvider(AudioPlayer player) {
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