package everyos.bot.luwu.run.command.modules.music;

import everyos.bot.chat4j.entity.ChatPermission;
import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.CommandData;
import reactor.core.publisher.Mono;

public class MusicFastForwardCommand extends GenericMusicCommand {
	public MusicFastForwardCommand() {
		super("command.music.fastforward", e->true, ChatPermission.SEND_MESSAGES, ChatPermission.NONE);
	}

	@Override
	protected Mono<Void> execute(CommandData data, ArgumentParser parser, MusicManager manager) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean requiresDJ() {
		return true;
	}	
}
