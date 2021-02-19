package everyos.bot.luwu.run.command.modules.music;

import everyos.bot.chat4j.entity.ChatPermission;
import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.CommandData;
import reactor.core.publisher.Mono;

public class MusicQueueCommand extends GenericMusicCommand {
	public MusicQueueCommand() {
		super("command.music.queue", e->true, ChatPermission.SEND_MESSAGES|ChatPermission.SEND_EMBEDS, ChatPermission.NONE);
	}

	@Override
	Mono<Void> execute(CommandData data, ArgumentParser parser, MusicManager manager) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	boolean requiresDJ() {
		return false;
	}

}
