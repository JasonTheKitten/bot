package everyos.discord.luwu.command.utility;

import discord4j.core.object.entity.Message;
import everyos.discord.luwu.command.CommandData;
import everyos.discord.luwu.command.ICommand;
import reactor.core.publisher.Mono;

public class OathCommand implements ICommand {
	@Override public Mono<?> execute(Message message, CommandData data, String argument) {
		return null;
	}
}
