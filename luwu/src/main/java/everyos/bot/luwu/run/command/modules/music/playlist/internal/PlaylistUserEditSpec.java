package everyos.bot.luwu.run.command.modules.music.playlist.internal;

public interface PlaylistUserEditSpec {
	PlaylistEditSpec getPlaylistEditSpec(String name);
	void deletePlaylist(String name);
	void createPlaylist(String name);
}
