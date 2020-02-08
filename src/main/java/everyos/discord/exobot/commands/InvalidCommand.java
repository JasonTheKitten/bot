package everyos.discord.exobot.commands;

import discord4j.core.object.entity.Message;

public class InvalidCommand implements ICommand {
	@Override public void execute(Message message, String argument) {	
		/*String prefix = GuildHelper.getGuildData(message.getGuild()).prefix;
		MessageHelper.send(message.getChannel(), "Invalid command\nRun `"+prefix+"help` for help", true);*/
	}

	@Override public String getHelp() {
		return " Undocumented";
	}

	@Override public COMMANDS getType() {
		return COMMANDS.Info;
	}
	
	@Override public String getFullHelp() {
		return " Undocumented";
	}
}
