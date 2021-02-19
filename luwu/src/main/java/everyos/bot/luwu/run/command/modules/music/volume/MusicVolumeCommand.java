package everyos.bot.luwu.run.command.modules.music.volume;

import everyos.bot.luwu.core.command.CommandContainer;
import everyos.bot.luwu.run.command.MultiCommand;

public class MusicVolumeCommand extends MultiCommand {
	public MusicVolumeCommand() {
		super("command.music.volume");
	}

	@Override
	public CommandContainer getCommands() {
		CommandContainer container = new CommandContainer();
		
		return container;
	}
}
