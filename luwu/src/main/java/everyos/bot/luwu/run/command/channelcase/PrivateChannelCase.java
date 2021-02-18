package everyos.bot.luwu.run.command.channelcase;

import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.CommandContainer;
import everyos.bot.luwu.core.command.CommandData;
import everyos.bot.luwu.run.command.modules.battle.AdventureCommands;
import everyos.bot.luwu.run.command.modules.easteregg.EasterEggCommands;
import everyos.bot.luwu.run.command.modules.fun.FunCommands;
import everyos.bot.luwu.run.command.modules.info.InfoCommands;
import reactor.core.publisher.Mono;

public class PrivateChannelCase extends CommandChannelCase {
	private static PrivateChannelCase instance;
	private CommandContainer commands;
	
	public PrivateChannelCase() {
		this.commands = new CommandContainer();
		
		commands.category("fun");
		AdventureCommands.installTo(commands);
		FunCommands.installTo(commands);
		
		commands.category("info");
		InfoCommands.installTo(commands);
		
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
		return "command.channelcase.private";
	}
	
	public static PrivateChannelCase get() {
		if (instance==null) instance = new PrivateChannelCase();
		return instance;
	}
}
