package everyos.discord.luwu.channelcase;

import java.util.HashMap;

import discord4j.core.object.entity.Message;
import everyos.discord.luwu.command.CommandData;
import everyos.discord.luwu.command.ICommand;
import everyos.discord.luwu.command.IGroupCommand;
import everyos.discord.luwu.command.info.HelpCommand;
import everyos.discord.luwu.command.utility.TicketManagerCommand;
import everyos.discord.luwu.localization.Localization;
import everyos.discord.luwu.parser.ArgumentParser;
import reactor.core.publisher.Mono;

public class TicketChannelCase implements IGroupCommand {
	private HashMap<String, ICommand> commands;
    public TicketChannelCase() {
        commands = new HashMap<String, ICommand>();
        commands.put("ticket", new TicketManagerCommand());
        commands.put("help", new HelpCommand());
    }
	
	@Override public Mono<?> execute(Message message, CommandData data, String argument) {
		String content = message.getContent();
        String trunc = ArgumentParser.getIfPrefix(content, data.prefixes);
        if (trunc == null) return Mono.empty();
        String command = ArgumentParser.getCommand(trunc);
        String arg = ArgumentParser.getArgument(trunc);

        if (commands.containsKey(command)) return commands.get(command).execute(message, data, arg);
        return Mono.empty();
	}
	
	@Override public HashMap<String, ICommand> getCommands(Localization locale) { return commands; }
}
