package everyos.discord.bot.command.utility;

import discord4j.core.object.entity.Message;
import everyos.discord.bot.command.CommandData;
import everyos.discord.bot.command.IGroupCommand;
import reactor.core.publisher.Mono;

public class TicketManagerCommand implements IGroupCommand {
	@Override public Mono<?> execute(Message message, CommandData data, String argument) {
		return null;
	}
}
