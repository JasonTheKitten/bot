package everyos.bot.luwu.run.command.modules.music.playlist.internal;

import java.util.Map;
import java.util.function.Consumer;

import everyos.bot.chat4j.entity.ChatUser;
import everyos.bot.luwu.core.database.DBArray;
import everyos.bot.luwu.core.database.DBDocument;
import everyos.bot.luwu.core.database.DBObject;
import everyos.bot.luwu.core.entity.Connection;
import everyos.bot.luwu.core.entity.User;
import reactor.core.publisher.Mono;

public class PlaylistUser extends User {
	protected PlaylistUser(Connection connection, ChatUser user, Map<String, DBDocument> documents) {
		super(connection, user, documents);
	}
	
	public Mono<PlaylistUserInfo> getInfo() {
		return getGlobalDocument().map(doc->{
			DBObject object = doc.getObject();
			DBObject playlistObject = object.getOrCreateObject("playlists", obj->{});
			
			return new PlaylistUserInfoImp(playlistObject);
		});
	}
	
	public Mono<Void> edit(Consumer<PlaylistUserEditSpec> func) {
		return getGlobalDocument().flatMap(doc->{
			DBObject object = doc.getObject();
			DBObject playlistObject = object.getOrCreateObject("playlists", obj->{});
			
			func.accept(new PlaylistUserEditSpecImp(playlistObject));
			
			return doc.save();
		});
	}
	
	private class PlaylistUserInfoImp implements PlaylistUserInfo {
		private DBObject object;

		public PlaylistUserInfoImp(DBObject object) {
			this.object = object;
		}

		@Override
		public PlaylistInfo[] getPlaylists() {
			PlaylistInfo[] rtn = new PlaylistInfo[object.getKeys().length];
			int i=0;
			for (String name: object.getKeys()) {
				rtn[i++] = new PlaylistInfoImp(name, object.getOrDefaultArray(name, null));
			}
			return rtn;
		}

		@Override
		public PlaylistInfo getPlaylistByName(String name) {
			if (!object.has(name)) return null;
			return new PlaylistInfoImp(name, object.getOrDefaultArray(name, null));
		}
	}
	
	private class PlaylistUserEditSpecImp implements PlaylistUserEditSpec {
		private DBObject object;

		public PlaylistUserEditSpecImp(DBObject object) {
			this.object = object;
		}
		
		@Override
		public PlaylistEditSpec getPlaylistEditSpec(String name) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void deletePlaylist(String name) {
			object.remove(name);
		}

		@Override
		public void createPlaylist(String name) {
			if (object.has(name)) return;
			object.createArray(name, a->{});
		}
	}
	
	private class PlaylistInfoImp implements PlaylistInfo {
		@SuppressWarnings("unused")
		private DBArray object;
		private String name;

		public PlaylistInfoImp(String name, DBArray object) {
			this.name = name;
			this.object = object;
		}

		@Override
		public String getName() {
			return name;
		}
		
		
	}

	public static PlaylistUserFactory type = new PlaylistUserFactory();
}
