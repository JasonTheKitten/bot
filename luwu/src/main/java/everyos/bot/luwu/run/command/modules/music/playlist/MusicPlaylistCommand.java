package everyos.bot.luwu.run.command.modules.music.playlist;

import everyos.bot.luwu.core.command.CommandContainer;
import everyos.bot.luwu.run.command.MultiCommand;

public class MusicPlaylistCommand extends MultiCommand {
	public MusicPlaylistCommand() {
		super("command.music.playlist");
	}

	@Override
	public CommandContainer getCommands() {
		CommandContainer container = new CommandContainer();
		
		return container;
	}
}
