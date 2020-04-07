package everyos.discord.bot.command.fun;

import discord4j.core.object.entity.Message;
import everyos.discord.bot.command.CommandData;
import everyos.discord.bot.command.ICommand;
import reactor.core.publisher.Mono;

public class BottleCommand implements ICommand { //Message in a bottle command
	@Override public Mono<?> execute(Message message, CommandData data, String argument) {
		return null;
	}
}
