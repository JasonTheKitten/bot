package everyos.bot.luwu.run.command.modules.chatlink.moderation;

import everyos.bot.luwu.core.command.Command;
import everyos.bot.luwu.core.command.CommandContainer;
import everyos.bot.luwu.run.command.MultiCommand;

public class LinkCommand extends MultiCommand implements Command {
	private CommandContainer commands;

	public LinkCommand() {
		super("command.link.moderation");
		
		this.commands = new CommandContainer();

        //Commands
        Command linkAcceptCommand = new LinkAcceptCommand();
        
        commands.registerCommand("command.link.accept", linkAcceptCommand);
	}

	@Override
	public CommandContainer getCommands() {
		return commands;
	}
}
