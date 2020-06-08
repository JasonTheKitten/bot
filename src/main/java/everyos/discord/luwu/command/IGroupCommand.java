package everyos.discord.luwu.command;

import java.util.HashMap;

import everyos.discord.luwu.localization.Localization;

public interface IGroupCommand extends ICommand {
	HashMap<String, ICommand> getCommands(Localization locale);
}
