package everyos.discord.exobot.util;

import everyos.discord.exobot.Statics;
import everyos.discord.exobot.commands.ICommand;

public class CommandHelper {
	public static void register(String name, ICommand command) {
		Statics.commands.put(name, command);
	}
}
