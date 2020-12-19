package everyos.bot.luwu.run.command.channelcase;

import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.CommandContainer;
import everyos.bot.luwu.core.command.CommandData;
import everyos.bot.luwu.run.command.modules.chatlink.setup.LinkCreateCommand;
import everyos.bot.luwu.run.command.modules.chatlink.setup.LinkJoinCommand;
import everyos.bot.luwu.run.command.modules.fun.CatCommand;
import everyos.bot.luwu.run.command.modules.fun.HugCommand;
import everyos.bot.luwu.run.command.modules.fun.UwUCommand;
import everyos.bot.luwu.run.command.modules.levelling.LevelCommand;
import everyos.bot.luwu.run.command.modules.moderation.ModerationCommands;
import everyos.bot.luwu.run.command.modules.music.MusicCommands;
import reactor.core.publisher.Mono;

public class DefaultChannelCase extends CommandChannelCase {
	private static DefaultChannelCase instance;
	private CommandContainer commands;
	
	public DefaultChannelCase() {
		this.commands = new CommandContainer();
		ModerationCommands.installTo(commands);
		MusicCommands.installTo(commands);
		commands.registerCommand("command.link.create", new LinkCreateCommand());
		commands.registerCommand("command.link.join", new LinkJoinCommand());
		commands.registerCommand("command.easteregg.cat", new CatCommand());
		commands.registerCommand("command.easteregg.uwu", new UwUCommand());
		commands.registerCommand("command.level", new LevelCommand());
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

	public static DefaultChannelCase get() {
		if (instance==null) instance = new DefaultChannelCase();
		return instance;
	}
}
