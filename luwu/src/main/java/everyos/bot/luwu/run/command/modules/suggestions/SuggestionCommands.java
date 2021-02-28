package everyos.bot.luwu.run.command.modules.suggestions;

import everyos.bot.luwu.core.command.CommandContainer;

public final class SuggestionCommands {
	private SuggestionCommands() {}
	
	public static void installTo(CommandContainer container) {
		container.registerCommand("command.suggestion", new SuggestionChannelCommand());
	}
}
