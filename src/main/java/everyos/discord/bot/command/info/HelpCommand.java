package everyos.discord.bot.command.info;

import discord4j.core.object.entity.Message;
import everyos.discord.bot.command.CommandData;
import everyos.discord.bot.command.ICommand;
import reactor.core.publisher.Mono;

public class HelpCommand implements ICommand {
	@Override public Mono<?> execute(Message message, CommandData data, String argument) {
		return null;
	}
	
	public Mono<?> showHelp(ICommand command) {
		return null;
	}
}
