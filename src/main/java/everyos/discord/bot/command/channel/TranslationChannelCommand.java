package everyos.discord.bot.command.channel;

import discord4j.core.object.entity.Message;
import everyos.discord.bot.annotation.Ignorable;
import everyos.discord.bot.command.CommandData;
import everyos.discord.bot.command.ICommand;
import reactor.core.publisher.Mono;

@Ignorable(id=9)
public class TranslationChannelCommand implements ICommand {
	@Override public Mono<?> execute(Message message, CommandData data, String argument) {
		return null;
	}
}
