package everyos.bot.luwu.core.command;

import everyos.bot.luwu.core.entity.Locale;

public class CommandEntry {
	private String label;
	private Command command;

	public CommandEntry(String label, Command command) {
		this.label = label;
		this.command = command;
	}
	
	public Command getIfMatches(String name, Locale locale) {
		if (name.equals(locale.localize(label))) return command;
		return null;
	}
}
