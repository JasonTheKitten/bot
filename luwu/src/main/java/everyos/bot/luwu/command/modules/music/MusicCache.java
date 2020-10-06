package everyos.bot.luwu.command.modules.music;

import java.util.HashMap;

import everyos.bot.luwu.entity.Channel;
import everyos.bot.luwu.entity.ChannelID;
import everyos.bot.luwu.entity.ServerID;
import reactor.core.publisher.Mono;

public class MusicCache {
	private HashMap<ChannelID, MusicManager> ccache = new HashMap<>();
	private HashMap<ServerID, MusicManager> scache = new HashMap<>();
	public Mono<MusicManager> getFromCache(Channel channel) {
		return Mono.just(getFromCache(channel.getID())); //TODO: ServerID instead
	}
	
	private MusicManager getFromCache(ChannelID channelID) {
		synchronized(ccache) {
			if (!ccache.containsKey(channelID)) {
				ccache.put(channelID, new MusicManager(()->ccache.remove(channelID)));
			}
			return ccache.get(channelID);
		}
	}
	private MusicManager getFromCache(ServerID serverID) {
		synchronized(scache) {
			if (!scache.containsKey(serverID)) {
				scache.put(serverID, new MusicManager(()->scache.remove(serverID)));
			}
			return scache.get(serverID);
		}
	}
	
	public static interface MusicCacheFinalizer {
		public void cleanup();
	}
}
