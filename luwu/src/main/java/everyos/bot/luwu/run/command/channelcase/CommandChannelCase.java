package everyos.bot.luwu.run.command.channelcase;

import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.ChannelCase;
import everyos.bot.luwu.core.command.Command;
import everyos.bot.luwu.core.command.CommandContainer;
import everyos.bot.luwu.core.command.CommandData;
import everyos.bot.luwu.core.command.GroupCommand;
import everyos.bot.luwu.core.exception.TextException;
import everyos.bot.luwu.core.functionality.channel.ChannelTextInterface;
import reactor.core.publisher.Mono;

public abstract class CommandChannelCase implements ChannelCase, GroupCommand {
	public Mono<Boolean> runCommands(CommandData data, ArgumentParser parser) {
		//Here we must
		// a) Query the prefixes, most likely a data.getChannel().getPrefixes()
		// b) Check if we match the prefixes
		// c) Lookup and run the command
		
		return eatPrefix(data, parser)
			.filter(v -> v)
			.flatMap(v -> runCommand(getCommands(), data, parser))
			.defaultIfEmpty(false);
	}
	
	private Mono<Boolean> eatPrefix(CommandData data, ArgumentParser parser) {
		//Query the prefixes
		//TODO: Return a locale instead
		return data.getChannel().getPrefixes().flatMap(prefixes->{
			for (String prefix: prefixes) {
				//Check if we match the prefixes
				if (parser.peek(prefix.length()).equals(prefix)) {
					parser.eat(prefix.length());
					return Mono.just(true);
				}
			}
			return Mono.just(false);
		});
	}
	
	private Mono<Boolean> runCommand(CommandContainer commands, CommandData data, ArgumentParser parser) {
		//Lookup the command
		Command c = commands.getCommand(parser.eat(), data.getLocale()); //TODO: Detect preferred locale
		if (c==null) return Mono.just(false);
		
		//Execute the command
		return c.run(data, parser)
			//Handle errors
			.onErrorResume(ex->{
				ChannelTextInterface channel = data.getChannel().getInterface(ChannelTextInterface.class);
				if (ex instanceof TextException) return channel.send(ex.getMessage()).then();
				return channel.send("Oops!, made an error. Logging that to console.").then(Mono.error(ex));
				//data.getLocale().localize("bot.error.logged"));
			})
			.then(Mono.just(true));
	}
}
