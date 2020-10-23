package everyos.bot.luwu.run.command.channelcase;

import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.CommandContainer;
import everyos.bot.luwu.core.command.CommandData;
import everyos.bot.luwu.run.command.modules.chatlink.setup.LinkCreateCommand;
import everyos.bot.luwu.run.command.modules.fun.CatCommand;
import everyos.bot.luwu.run.command.modules.fun.HugCommand;
import everyos.bot.luwu.run.command.modules.moderation.ModerationCommands;
import reactor.core.publisher.Mono;

public class DefaultChannelCase extends CommandChannelCase {
	private static DefaultChannelCase instance;
	private CommandContainer commands;
	
	public DefaultChannelCase() {
		this.commands = new CommandContainer();
		ModerationCommands.installTo(commands);
		commands.registerCommand("command.link.create", new LinkCreateCommand());
		commands.registerCommand("command.easteregg.cat", new CatCommand());
		commands.registerCommand("command.hug", new HugCommand());
	}

	@Override public Mono<Void> execute(CommandData data, ArgumentParser parser) {
		return runCommands(commands, data, parser).then();
	}

	public static DefaultChannelCase get() {
		if (instance==null) instance = new DefaultChannelCase();
		return instance;
	}
}
