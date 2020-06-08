package everyos.discord.luwu.command.channel;

import java.util.HashMap;

import discord4j.core.object.entity.Message;
import everyos.discord.luwu.command.CommandData;
import everyos.discord.luwu.command.ICommand;
import everyos.discord.luwu.command.IGroupCommand;
import everyos.discord.luwu.localization.Localization;
import reactor.core.publisher.Mono;

public class MedalBoardCommand implements IGroupCommand {
	@Override public Mono<?> execute(Message message, CommandData data, String argument) {
		return null;
	}

	@Override public HashMap<String, ICommand> getCommands(Localization locale) {
		return null;
	}
}
