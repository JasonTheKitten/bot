package everyos.bot.luwu.run.command.channelcase;

import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.CommandContainer;
import everyos.bot.luwu.core.command.CommandData;
import everyos.bot.luwu.run.command.modules.chatlink.setup.LinkSetupCommands;
import everyos.bot.luwu.run.command.modules.easteregg.EasterEggCommands;
import everyos.bot.luwu.run.command.modules.fun.FunCommands;
import everyos.bot.luwu.run.command.modules.info.InfoCommands;
import everyos.bot.luwu.run.command.modules.levelling.LevelCommands;
import everyos.bot.luwu.run.command.modules.moderation.ModerationCommands;
import everyos.bot.luwu.run.command.modules.music.MusicCommands;
import reactor.core.publisher.Mono;

public class DefaultChannelCase extends CommandChannelCase {
	private static DefaultChannelCase instance;
	private CommandContainer commands;
	
	public DefaultChannelCase() {
		this.commands = new CommandContainer();
		
		commands.category("moderation");
		ModerationCommands.installTo(commands);
		
		commands.category("fun");
		MusicCommands.installTo(commands);
		FunCommands.installTo(commands);
		
		commands.category("info");
		InfoCommands.installTo(commands);
		
		commands.category("channel");
		LinkSetupCommands.installTo(commands);
		
		commands.category("utility");
		LevelCommands.installTo(commands);
		
		commands.category(null);
		EasterEggCommands.installTo(commands);
	}

	@Override
	public Mono<Void> execute(CommandData data, ArgumentParser parser) {
		return runCommands(data, parser).then();
	}
	
	@Override
	public CommandContainer getCommands() {
		return commands;
	}
	
	@Override
	public String getID() {
		return "command.channelcase.default";
	}

	public static DefaultChannelCase get() {
		if (instance==null) instance = new DefaultChannelCase();
		return instance;
	}
}
