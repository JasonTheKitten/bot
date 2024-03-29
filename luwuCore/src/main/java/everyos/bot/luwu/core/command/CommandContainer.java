package everyos.bot.luwu.core.command;

import java.util.ArrayList;

import everyos.bot.luwu.core.entity.Locale;

public class CommandContainer {
	private ArrayList<CommandEntry> commands = new ArrayList<>();
	private String category = "default";
	
	public void registerCommand(String label, Command command) {
		commands.add(new CommandEntry(label, command, category));
	}
	public Command getCommand(String name, Locale locale) {
		for (CommandEntry entry: commands.toArray(new CommandEntry[commands.size()])) {
			Command c = entry.getIfMatches(name, locale);
			if (c!=null) return c;
		}
		return null;
	}
	public CommandEntry[] getAll() {
		return commands.toArray(new CommandEntry[commands.size()]);
	}
	public void category(String category) {
		this.category = category;
	}
}
