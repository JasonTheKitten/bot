package everyos.discord.exobot.commands;

import discord4j.core.object.entity.Message;

public class UnbanCommand implements ICommand {
	@Override public void execute(Message message, String argument) {
		
	}

	@Override public String getHelp() {
		return "<user> Unbans the specified user";
	}

	@Override public COMMANDS getType() {
		return COMMANDS.Incomplete; //COMMANDS.Moderation;
	}
	
	@Override public String getFullHelp() {
		return "**<user>** The user to unban";
	}
}
