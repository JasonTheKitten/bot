package everyos.bot.luwu.run.command.modules.info;

import everyos.bot.luwu.core.annotation.Help;
import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.Command;
import everyos.bot.luwu.core.command.CommandData;
import reactor.core.publisher.Mono;

@Help(help="command.help.help", ehelp="command.help.help.extended")
public class HelpCommand implements Command {
	@Override
	public Mono<Void> execute(CommandData data, ArgumentParser parser) {
		String commandName = parser.getRemaining();
		return resolveCommand(data, commandName.strip()).flatMap(command->{
			return Mono.empty();
		});
	}

	private Mono<Command> resolveCommand(CommandData data, String remaining) {
		return data.getBotEngine().getChannelCase(data).flatMap(channelCase->{
			Command resolved = channelCase;
			while (!remaining.isEmpty()) {
				
			}
			
			return Mono.just(resolved);
		});
	}
}
