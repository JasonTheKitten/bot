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
        Command linkGlobalMuteCommand = new LinkGlobalMuteCommand();
        Command linkLocalMuteCommand = new LinkLocalMuteCommand();
        Command linkDisambiguationMuteCommand = new LinkDisambiguationMuteCommand();
        Command linkOptCommand = new LinkOptCommand();
        
        commands.registerCommand("command.link.accept", linkAcceptCommand);
        commands.registerCommand("command.link.mute.global", linkGlobalMuteCommand);
        commands.registerCommand("command.link.mute.local", linkLocalMuteCommand);
        commands.registerCommand("command.link.opt", linkOptCommand);
        
        commands.category(null);
        commands.registerCommand("command.link.mute.disambiguation", linkDisambiguationMuteCommand);
	}

	@Override
	public CommandContainer getCommands() {
		return commands;
	}
}
