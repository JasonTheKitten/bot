package everyos.bot.luwu.run.command.modules.music;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import everyos.bot.luwu.core.entity.Server;
import everyos.bot.luwu.core.entity.ServerID;
import everyos.bot.luwu.core.entity.User;
import everyos.bot.luwu.core.entity.UserID;
import reactor.core.publisher.Mono;

public class MusicCache {
	private Map<ServerID, MusicManager> scache = new ConcurrentHashMap<>();
	public Mono<MusicManager> getFromCache(Server server) {
		return Mono.justOrEmpty(getFromCache(server.getID()));
	}
	
	private MusicManager getFromCache(ServerID serverID) {
		if (!scache.containsKey(serverID)) {
			return null;
		}
		return scache.get(serverID);
	}
	
	private Map<UserID, ServerID> ucache = new ConcurrentHashMap<>();
	public Mono<MusicManager> getFromCache(User user) {
		return Mono.justOrEmpty(getFromCache(user.getID()));
	}
	
	private MusicManager getFromCache(UserID userID) {
		if (!ucache.containsKey(userID)) {
			return null;
		}
		return getFromCache(ucache.get(userID));
	}
	
	public Mono<MusicManager> createToCache(Server server) {
		MusicManager manager = new MusicManager(()->{
			scache.remove(server.getID());
		});
		scache.put(server.getID(), manager);
		return Mono.just(manager);
	}
	
	public static interface MusicCacheFinalizer {
		public void cleanup();
	}
}
