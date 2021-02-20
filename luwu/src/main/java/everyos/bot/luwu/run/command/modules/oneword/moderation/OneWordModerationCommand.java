package everyos.bot.luwu.run.command.modules.oneword.moderation;

import everyos.bot.luwu.core.command.Command;
import everyos.bot.luwu.core.command.CommandContainer;
import everyos.bot.luwu.run.command.MultiCommand;

public class OneWordModerationCommand extends MultiCommand {

	public OneWordModerationCommand() {
		super("command.oneword.moderation");
	}

	@Override
	public CommandContainer getCommands() {
		CommandContainer commands = new CommandContainer();
		
		Command resetCommand = new OneWordResetCommand();
		Command lastUserCommand = new OneWordLastUserCommand();
		Command removeCommand = new OneWordRemoveCommand();
		
		commands.registerCommand("command.oneword.moderation.reset", resetCommand);
		commands.registerCommand("command.oneword.moderation.lastuser", lastUserCommand);
		commands.registerCommand("command.oneword.moderation.remove", removeCommand);
		
		return commands;
	}

}
