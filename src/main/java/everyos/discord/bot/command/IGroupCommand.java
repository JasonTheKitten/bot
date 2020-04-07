package everyos.discord.bot.command;

import java.util.HashMap;

import everyos.discord.bot.localization.Localization;

public interface IGroupCommand extends ICommand {
	HashMap<String, ICommand> getCommands(Localization locale);
}
