package everyos.discord.bot.usercase;

import discord4j.core.object.entity.Message;
import everyos.discord.bot.ShardInstance;
import everyos.discord.bot.command.CommandData;
import everyos.discord.bot.command.ICommand;
import reactor.core.publisher.Mono;

public class IgnoreUserCase implements ICommand {
	public IgnoreUserCase(ShardInstance shard) {}

	@Override public Mono<?> execute(Message message, CommandData data, String argument) {
		return Mono.empty();
	}
}
