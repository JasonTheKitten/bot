package everyos.bot.luwu.run.command.modules.music.playlist;

import everyos.bot.chat4j.entity.ChatPermission;
import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.CommandData;
import everyos.bot.luwu.run.command.modules.music.GenericMusicCommand;
import everyos.bot.luwu.run.command.modules.music.MusicManager;
import reactor.core.publisher.Mono;

public class MusicPlaylistPlayCommand extends GenericMusicCommand {
	public MusicPlaylistPlayCommand() {
		super("command.music.playlist.play", e->true, ChatPermission.SEND_MESSAGES, ChatPermission.NONE);
	}

	@Override
	protected Mono<Void> execute(CommandData data, ArgumentParser parser, MusicManager manager) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean requiresDJ() {
		// TODO Auto-generated method stub
		return false;
	}
	
}
