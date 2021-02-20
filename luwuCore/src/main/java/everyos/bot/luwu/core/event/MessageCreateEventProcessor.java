package everyos.bot.luwu.core.event;

import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.CommandData;
import everyos.bot.luwu.core.entity.event.MessageCreateEvent;
import everyos.bot.luwu.core.exception.TextException;
import everyos.bot.luwu.core.functionality.channel.ChannelTextInterface;
import reactor.core.publisher.Mono;

public class MessageCreateEventProcessor {
	public static Mono<Void> apply(MessageCreateEvent event) {
		// Step 1: Determine the user type, and also construct command arguments
		
		// You see. The annoying thing about asynchronous programming is how much work it takes to get just 3 variables.
		// I mean, we could use .zipWith, but that's not much better
		return event.getMessage().flatMap(message->{
			return message.getChannel().flatMap(channel->{
				return event.getSender()
					.filter(sender->!sender.isBot())
					.flatMap(sender->sender.asMemberOf(channel))
					.map(sender->new CommandData(message, sender, channel));
			});
		}).flatMap(data->{
			ArgumentParser parser = event.getClient().getBehaviour().createParser(event.getConnection(), data.getMessage().getContent().orElse(""));
			return event.getConnection().getBotEngine().getUserCase(data)
				.flatMap(command->command.execute(data, parser)) //Step 2: We execute the default command
				.onErrorResume(ex->{
					ChannelTextInterface channel = data.getChannel().getInterface(ChannelTextInterface.class);
					if (ex instanceof TextException) return channel.send(ex.getMessage()).then();
					return Mono.error(ex);
				}); 
		}).then();
	}
}
