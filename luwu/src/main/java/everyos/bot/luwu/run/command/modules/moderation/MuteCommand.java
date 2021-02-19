package everyos.bot.luwu.run.command.modules.moderation;

import everyos.bot.chat4j.entity.ChatPermission;
import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.CommandData;
import everyos.bot.luwu.run.command.CommandBase;
import reactor.core.publisher.Mono;

public class MuteCommand extends CommandBase {
	@SuppressWarnings("unused")
	private boolean isMute;

	public MuteCommand(boolean isMute) {
		super(isMute?"command.mute":"command.unmute",
			e->true, ChatPermission.MANAGE_ROLES|ChatPermission.MANAGE_MEMBERS, ChatPermission.MANAGE_MEMBERS);
		this.isMute = isMute;
	}

	@Override
	public Mono<Void> execute(CommandData data, ArgumentParser parser) {
		return Mono.empty();
	}
}
