package everyos.bot.luwu.run.command.modules.music;

import everyos.bot.chat4j.entity.ChatPermission;
import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.CommandData;
import reactor.core.publisher.Mono;

public class MusicSwapCommand extends GenericMusicCommand {
	public MusicSwapCommand() {
		super("command.music.swap", e->true, ChatPermission.SEND_MESSAGES, ChatPermission.NONE);
	}

	@Override
	Mono<Void> execute(CommandData data, ArgumentParser parser, MusicManager manager) {
		return Mono.empty();
	}

	@Override
	boolean requiresDJ() {
		return true;
	}
}
