package everyos.bot.luwu.run.command.modules.levelling;

import everyos.bot.luwu.core.command.Command;
import everyos.bot.luwu.core.command.CommandContainer;
import everyos.bot.luwu.run.command.MultiCommand;

public class LevelCommand extends MultiCommand {
	private final CommandContainer commands;

	public LevelCommand() {
		super("command.level");
		
		this.commands = new CommandContainer();

        //Commands
        Command levelCheckCommand = new LevelCheckCommand();
        Command levelEnableCommand = new LevelEnableCommand(true);
        Command levelDisableCommand = new LevelEnableCommand(false);
        Command levelMessageCommand = new LevelMessageCommand();
        
        commands.registerCommand("command.level.check", levelCheckCommand);
        commands.registerCommand("command.level.enable", levelEnableCommand);
        commands.registerCommand("command.level.disable", levelDisableCommand);
        commands.registerCommand("command.level.message", levelMessageCommand);
	}

	@Override
	public CommandContainer getCommands() {
		return commands;
	}
}
