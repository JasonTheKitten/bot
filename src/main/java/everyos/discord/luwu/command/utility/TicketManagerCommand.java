package everyos.discord.luwu.command.utility;

import java.util.HashMap;

import discord4j.core.object.entity.Message;
import everyos.discord.luwu.annotation.Help;
import everyos.discord.luwu.command.CategoryEnum;
import everyos.discord.luwu.command.CommandData;
import everyos.discord.luwu.command.ICommand;
import everyos.discord.luwu.command.IGroupCommand;
import everyos.discord.luwu.localization.Localization;
import everyos.discord.luwu.localization.LocalizedString;
import reactor.core.publisher.Mono;

@Help(help=LocalizedString.TicketManagerCommandHelp, ehelp = LocalizedString.TicketManagerCommandExtendedHelp, category=CategoryEnum.Utility)
public class TicketManagerCommand implements IGroupCommand {
	private HashMap<Localization, HashMap<String, ICommand>> lcommands;

    public TicketManagerCommand() {
        HashMap<String, ICommand> commands;
        lcommands = new HashMap<Localization, HashMap<String, ICommand>>();

        //Commands

        //en_US
        commands = new HashMap<String, ICommand>();
        lcommands.put(Localization.en_US, commands);
    }
	
	@Override public Mono<?> execute(Message message, CommandData data, String argument) {
		return Mono.empty();
	}
	
	@Override public HashMap<String, ICommand> getCommands(Localization locale) { return lcommands.get(locale); }
}
