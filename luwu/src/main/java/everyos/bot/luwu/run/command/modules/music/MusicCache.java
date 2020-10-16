package everyos.bot.luwu.run.command.modules.music;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import everyos.bot.luwu.core.entity.Channel;
import everyos.bot.luwu.core.entity.ServerID;
import reactor.core.publisher.Mono;

public class MusicCache {
	private Map<ServerID, MusicManager> scache = new ConcurrentHashMap<>();
	public Mono<MusicManager> getFromCache(Channel channel) {
		return channel.getServer().map(server->getFromCache(server.getID())); //TODO: ServerID instead
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
