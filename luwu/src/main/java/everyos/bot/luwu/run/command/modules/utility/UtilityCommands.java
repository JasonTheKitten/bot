package everyos.bot.luwu.run.command.modules.utility;

import everyos.bot.luwu.core.command.CommandContainer;

public final class UtilityCommands {
	private UtilityCommands() {}
	
	public static void installTo(CommandContainer container) {
		container.registerCommand("command.dictionary", new DictionaryCommand());
		container.registerCommand("command.ghost", new GhostCommand());
		container.registerCommand("command.embed", new EmbedCommand());
		container.registerCommand("command.profile", new ProfileCommand());
		container.registerCommand("command.suggest", new SuggestCommand());
	}
}
