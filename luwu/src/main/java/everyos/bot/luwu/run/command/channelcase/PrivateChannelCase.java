package everyos.bot.luwu.run.command.channelcase;

import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.CommandContainer;
import everyos.bot.luwu.core.command.CommandData;
import everyos.bot.luwu.run.command.modules.easteregg.CatCommand;
import everyos.bot.luwu.run.command.modules.easteregg.UwUCommand;
import everyos.bot.luwu.run.command.modules.fun.HugCommand;
import everyos.bot.luwu.run.command.modules.music.MusicCommands;
import reactor.core.publisher.Mono;

public class PrivateChannelCase extends CommandChannelCase {
	private static PrivateChannelCase instance;
	private CommandContainer commands;
	
	public PrivateChannelCase() {
		this.commands = new CommandContainer();
		MusicCommands.installTo(commands);
		commands.registerCommand("command.easteregg.cat", new CatCommand());
		commands.registerCommand("command.easteregg.uwu", new UwUCommand());
		commands.registerCommand("command.hug", new HugCommand());
	}

	@Override
	public Mono<Void> execute(CommandData data, ArgumentParser parser) {
		return runCommands(data, parser).then();
	}

	@Override
	public CommandContainer getCommands() {
		return commands;
	}
	
	public static PrivateChannelCase get() {
		if (instance==null) instance = new PrivateChannelCase();
		return instance;
	}
}
