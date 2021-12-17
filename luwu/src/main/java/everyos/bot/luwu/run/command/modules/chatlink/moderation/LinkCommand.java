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
        Command linkGlobalUnmuteCommand = new LinkGlobalUnmuteCommand();
        Command linkLocalUnmuteCommand = new LinkLocalUnmuteCommand();
        Command linkDisambiguationMuteCommand = new LinkDisambiguationMuteCommand();
        Command linkOptCommand = new LinkOptCommand();
        Command linkOptUserCommand = new LinkOptUserCommand();
        Command linkOptOutCommand = new LinkOptOutCommand();
        Command linkRulesCommand = new LinkRulesCommand();
        Command linkSetRulesCommand = new LinkSetRulesCommand();
        Command linkIDCommand = new LinkIDCommand();
        
        commands.registerCommand("command.link.accept", linkAcceptCommand);
        commands.registerCommand("command.link.mute.global", linkGlobalMuteCommand);
        commands.registerCommand("command.link.mute.local", linkLocalMuteCommand);
        commands.registerCommand("command.link.unmute.global", linkGlobalUnmuteCommand);
        commands.registerCommand("command.link.unmute.local", linkLocalUnmuteCommand);
        commands.registerCommand("command.link.opt", linkOptCommand);
        commands.registerCommand("command.link.optuser", linkOptUserCommand);
        commands.registerCommand("command.link.optout", linkOptOutCommand);
        commands.registerCommand("command.link.rules", linkRulesCommand);
        commands.registerCommand("command.link.setrules", linkSetRulesCommand);
        commands.registerCommand("command.link.id", linkIDCommand);
        
        commands.category(null);
        commands.registerCommand("command.link.mute.disambiguation", linkDisambiguationMuteCommand);
	}

	@Override
	public CommandContainer getCommands() {
		return commands;
	}
}
