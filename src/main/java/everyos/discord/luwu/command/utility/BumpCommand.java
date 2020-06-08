package everyos.discord.luwu.command.utility;

import discord4j.core.object.entity.Message;
import everyos.discord.luwu.command.CommandData;
import everyos.discord.luwu.command.ICommand;
import reactor.core.publisher.Mono;

public class BumpCommand implements ICommand {
	@Override public Mono<?> execute(Message message, CommandData data, String argument) {
		//So the idea is that we keep track of 5 servers, and you have to join a bumped server to bump yours
		return null;
	}
}
