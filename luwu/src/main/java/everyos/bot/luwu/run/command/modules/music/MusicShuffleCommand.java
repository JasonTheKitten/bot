package everyos.bot.luwu.run.command.modules.music;

import everyos.bot.chat4j.entity.ChatPermission;
import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.CommandData;
import reactor.core.publisher.Mono;

public class MusicShuffleCommand extends GenericMusicCommand {
	public MusicShuffleCommand() {
		super("command.music.shuffle", e->true, ChatPermission.SEND_MESSAGES, ChatPermission.NONE);
	}

	@Override
	Mono<Void> execute(CommandData data, ArgumentParser parser, MusicManager manager) {
		//manager.getQueue().
		return Mono.empty();
	}

	@Override
	boolean requiresDJ() {
		return true;
	}
}
