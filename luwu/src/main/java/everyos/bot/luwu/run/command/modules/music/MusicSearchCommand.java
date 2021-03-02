package everyos.bot.luwu.run.command.modules.music;

import everyos.bot.chat4j.entity.ChatPermission;
import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.CommandData;
import reactor.core.publisher.Mono;

public class MusicSearchCommand extends GenericMusicCommand {
	public MusicSearchCommand() {
		super("command.music.search", e->true,
			ChatPermission.SEND_MESSAGES|ChatPermission.SEND_EMBEDS|ChatPermission.ADD_REACTIONS|ChatPermission.VC_CONNECT|ChatPermission.VC_SPEAK,
			ChatPermission.NONE);
	}

	@Override
	protected Mono<Void> execute(CommandData data, ArgumentParser parser, MusicManager manager) {
		return null;
	}

	@Override
	protected boolean requiresDJ() {
		return true;
	}
}
