package everyos.bot.luwu.run.command.modules.music;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import everyos.bot.luwu.core.entity.Channel;
import everyos.bot.luwu.core.entity.ServerID;
import everyos.bot.luwu.core.entity.User;
import everyos.bot.luwu.core.entity.UserID;
import reactor.core.publisher.Mono;

public class MusicCache {
	private Map<ServerID, MusicManager> scache = new ConcurrentHashMap<>();
	public Mono<MusicManager> getFromCache(Channel channel) {
		return channel.getServer().map(server->getFromCache(server.getID()));
	}
	
	private MusicManager getFromCache(ServerID serverID) {
		if (!scache.containsKey(serverID)) {
			return scache.put(serverID, new MusicManager(()->scache.remove(serverID)));
		}
		return scache.get(serverID);
	}
	
	private Map<UserID, MusicManager> ucache = new ConcurrentHashMap<>();
	public Mono<MusicManager> getFromCache(User user) {
		return Mono.just(getFromCache(user.getID()));
	}
	
	private MusicManager getFromCache(UserID userID) {
		if (!ucache.containsKey(userID)) {
			return ucache.put(userID, new MusicManager(()->ucache.remove(userID)));
		}
		return ucache.get(userID);
	}
	
	public static interface MusicCacheFinalizer {
		public void cleanup();
	}
}
