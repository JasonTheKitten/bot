package everyos.bot.luwu.run.command.modules.music.playlist.internal;

public interface PlaylistUserInfo {
	PlaylistInfo[] getPlaylists();
	PlaylistInfo getPlaylistByName(String name);
}
