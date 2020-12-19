package everyos.bot.luwu.run.command.modules.music;

import everyos.bot.luwu.core.command.CommandContainer;

public class MusicCommands {
	public static void installTo(CommandContainer commands) {
		commands.registerCommand("command.music", new MusicCommand());
		commands.registerCommand("command.music.short", new MusicCommand()); //TODO: As alias
	}
}
