package everyos.bot.luwu.run.command.channelcase;

import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.CommandContainer;
import everyos.bot.luwu.core.command.CommandData;
import everyos.bot.luwu.run.command.modules.moderation.ModerationCommands;
import reactor.core.publisher.Mono;

public class DefaultChannelCase extends CommandChannelCase {
	private static DefaultChannelCase instance;
	private CommandContainer commands;
	
	public DefaultChannelCase() {
		this.commands = new CommandContainer();
		ModerationCommands.installTo(commands);
	}

	@Override public Mono<Void> execute(CommandData data, ArgumentParser parser) {
		return runCommands(commands, data, parser).then();
	}

	public static DefaultChannelCase get() {
		if (instance==null) instance = new DefaultChannelCase();
		return instance;
	}
}
