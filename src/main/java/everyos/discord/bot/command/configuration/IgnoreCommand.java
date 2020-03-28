package everyos.discord.bot.command.configuration;

import discord4j.core.object.entity.Message;
import everyos.discord.bot.command.CommandData;
import everyos.discord.bot.command.ICommand;
import reactor.core.publisher.Mono;

public class IgnoreCommand implements ICommand {
	@Override public Mono<?> execute(Message message, CommandData data, String argument) {
		return null;
	}
}
