package everyos.bot.luwu.run.command.channelcase;

import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.ChannelCase;
import everyos.bot.luwu.core.command.Command;
import everyos.bot.luwu.core.command.CommandContainer;
import everyos.bot.luwu.core.command.CommandData;
import everyos.bot.luwu.core.command.GroupCommand;
import everyos.bot.luwu.core.entity.Locale;
import everyos.bot.luwu.core.exception.TextException;
import everyos.bot.luwu.core.functionality.channel.ChannelTextInterface;
import everyos.bot.luwu.run.command.modules.configuration.prefix.PrefixServer;
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
		return data.getChannel()
			.getServer()
			.flatMap(server->server.as(PrefixServer.type))
			.flatMap(server->server.getInfo())
			.switchIfEmpty(PrefixServer.empty(data.getConnection()))
			.flatMap(info->{
				for (String prefix: info.getPrefixes()) {
					//Check if we match the prefixes
					if (parser.peek(prefix.length()).equals(prefix)) {
						parser.eat(prefix.length());
						return Mono.just(true);
					}
				}
				
				String[] prefixes = new String[] {
					"<@" + data.getConnection().getSelfID() + ">",
					"<@!" + data.getConnection().getSelfID() + ">"
				};
				
				for (String prefix: prefixes) {
					if (parser.peek(prefix.length()).equals(prefix)) {
						parser.eat(prefix.length());
						
						if (parser.isEmpty()) {
							Locale locale = data.getLocale();
							
							// Kind of not how TextException was designed to be used
							return Mono.error(new TextException(
								locale.localize("command.easteregg.prefix.message")));
						}
						
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
				return channel.send(data.getLocale().localize("command.error.logged")).then(Mono.error(ex));
			})
			.then(Mono.just(true));
	}
}
