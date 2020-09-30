package everyos.bot.luwu.command.channelcase;

import everyos.bot.chat4j.functionality.channel.ChatChannelTextInterface;
import everyos.bot.luwu.command.Command;
import everyos.bot.luwu.command.CommandContainer;
import everyos.bot.luwu.command.CommandData;
import everyos.bot.luwu.command.modules.moderation.ModerationCommands;
import everyos.bot.luwu.exception.TextException;
import everyos.bot.luwu.parser.ArgumentParser;
import reactor.core.publisher.Mono;

public class DefaultChannelCase extends ChannelCase {
	private static DefaultChannelCase instance;
	private CommandContainer commands;
	
	public DefaultChannelCase() {
		this.commands = new CommandContainer();
		ModerationCommands.installTo(commands);
	}

	@Override public Mono<?> execute(CommandData data, ArgumentParser parser) {
		//Here we must
		// a) Query the prefixes, most likely a data.getChannel().getPrefixes()
		// b) Check if we match the prefixes
		// c) Lookup and run the command
		
		return data.getChannel().getPrefixes().flatMap(prefixes->{
			for (String prefix: prefixes) {
				if (parser.peek(prefix.length()).equals(prefix)) {
					parser.eat(prefix.length());
					Command c = commands.getCommand(parser.eat(), data.getLocale()); //TODO: Detect preferred locale
					if (c!=null) {
						return c.execute(data, parser)
							.cast(Object.class)
							.onErrorResume(ex->{
								ChatChannelTextInterface channel = data.getChannel().getInterface(ChatChannelTextInterface.class);
								if (!(ex instanceof TextException)) {
									return channel.send("Oops!, made an error. Logging that to console.")
										.then(Mono.error(ex));
									//data.getLocale().localize("bot.error.logged"));
								}
								return channel.send(ex.getMessage());
							});
					}
				}
			}
			return Mono.empty();
		});
	}

	public static DefaultChannelCase get() {
		if (instance==null) instance = new DefaultChannelCase();
		return instance;
	}
}
