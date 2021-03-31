package everyos.bot.luwu.core.command;

import everyos.bot.luwu.core.entity.Locale;

public class CommandEntry {
	private String label;
	private Command command;
	private String category;

	public CommandEntry(String label, Command command, String category) {
		this.label = label;
		this.command = command;
		this.category = category;
	}
	
	public Command getIfMatches(String name, Locale locale) {
		if (name.equalsIgnoreCase(locale.localize(label))) return command;
		return null;
	}

	public String getLabel() {
		return label;
	}
	public Command getRaw() {
		return command;
	}
	public String getCategory() {
		return category;
	}
}
