package everyos.discord.exobot.commands;

import discord4j.core.object.entity.Message;

public class RemindCommand implements ICommand {
	@Override public void execute(Message message, String argument) {}

	@Override public String getHelp() {
		return "<user><days/login><text> Reminds a user some text";
	}

	@Override public String getFullHelp() {
		return 
				"**<user>** user to remind\n"+
				"**<days/login>** - something\n"+
				"**<text>** text to remind user";
	}

	@Override public COMMANDS getType() {
		return COMMANDS.Incomplete;
	}
}
