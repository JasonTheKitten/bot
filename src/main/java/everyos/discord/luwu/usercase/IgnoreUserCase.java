package everyos.discord.luwu.usercase;

import java.util.HashMap;

import discord4j.core.object.entity.Message;
import everyos.discord.luwu.BotInstance;
import everyos.discord.luwu.command.CommandData;
import everyos.discord.luwu.command.ICommand;
import everyos.discord.luwu.command.IGroupCommand;
import everyos.discord.luwu.localization.Localization;
import reactor.core.publisher.Mono;

public class IgnoreUserCase implements ICommand, IGroupCommand {
	public IgnoreUserCase(BotInstance bot) {}

	@Override public Mono<?> execute(Message message, CommandData data, String argument) {
		return Mono.empty();
	}

	@Override public HashMap<String, ICommand> getCommands(Localization locale) { return null; }
}
