package everyos.discord.luwu.command.configuration;

import discord4j.core.object.entity.Message;
import everyos.discord.luwu.command.CommandData;
import everyos.discord.luwu.command.ICommand;
import reactor.core.publisher.Mono;

public class LanguageCommand implements ICommand {
	@Override public Mono<?> execute(Message message, CommandData data, String argument) {
		return Mono.empty();
	}
}
