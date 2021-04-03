package everyos.bot.luwu.run.command.modules.currency;

import everyos.bot.luwu.core.command.Command;
import everyos.bot.luwu.core.command.CommandContainer;
import everyos.bot.luwu.run.command.MultiCommand;

public class FethCommand extends MultiCommand {
	private final CommandContainer commands;

	public FethCommand() {
		super("command.feth");
		
		this.commands = new CommandContainer();

        //Commands
        Command fethCheckCommand = new FethCheckCommand();
        Command fethEnableCommand = new FethEnableCommand(true);
        Command fethDisableCommand = new FethEnableCommand(false);
        Command fethDailyCommand = new FethDailyCommand();
        Command fethGiveCommand = new FethGiveCommand();
        
        commands.registerCommand("command.feth.check", fethCheckCommand);
        commands.registerCommand("command.feth.enable", fethEnableCommand);
        commands.registerCommand("command.feth.disable", fethDisableCommand);
        commands.registerCommand("command.feth.daily", fethDailyCommand);
        commands.registerCommand("command.feth.give", fethGiveCommand);
        //TODO: Leaderboard
	}

	@Override
	public CommandContainer getCommands() {
		return commands;
	}
}
