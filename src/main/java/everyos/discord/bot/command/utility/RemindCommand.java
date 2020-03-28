package everyos.discord.bot.command.utility;

import java.util.concurrent.atomic.AtomicReference;

import discord4j.core.object.entity.Message;
import everyos.discord.bot.command.CommandData;
import everyos.discord.bot.command.ICommand;
import reactor.core.publisher.Mono;

public class RemindCommand implements ICommand {
	@Override public Mono<?> execute(Message message, CommandData data, String argument) {
		return message.getChannel().flatMap(channel->{
			AtomicReference<Mono<?>> mono = new AtomicReference<Mono<?>>();
			
			return mono.get();
		});
	}
}
