package everyos.bot.luwu.run.command.modules.tickets.channel;

import everyos.bot.luwu.core.client.ArgumentParser;
import everyos.bot.luwu.core.command.CommandContainer;
import everyos.bot.luwu.core.command.CommandData;
import everyos.bot.luwu.run.command.channelcase.CommandChannelCase;
import everyos.bot.luwu.run.command.modules.channel.ResetChannelCommand;
import everyos.bot.luwu.run.command.modules.info.InfoCommands;
import everyos.bot.luwu.run.command.modules.moderation.ModerationCommands;
import everyos.bot.luwu.run.command.modules.tickets.manager.TicketManagerCommand;
import reactor.core.publisher.Mono;

public class TicketChannelCase extends CommandChannelCase {
	private CommandContainer commands;

	@Override
	public CommandContainer getCommands() {
		this.commands = new CommandContainer();
		
		commands.category("default");
		commands.registerCommand("command.ticket.manager", new TicketManagerCommand());
		commands.registerCommand("command.resetchannel", new ResetChannelCommand());
		
		commands.category("moderation");
		ModerationCommands.installTo(commands);
		
		commands.category("info");
		InfoCommands.installTo(commands);
		
		return commands;
	}

	@Override
	public Mono<Void> execute(CommandData data, ArgumentParser parser) {
		return runCommands(data, parser).then();
	}
	
	@Override
	public String getID() {
		return "command.ticket.channelcase";
	}
	
	private static TicketChannelCase instance = new TicketChannelCase();
	
	public static TicketChannelCase get() {
		return instance;
	}
}
