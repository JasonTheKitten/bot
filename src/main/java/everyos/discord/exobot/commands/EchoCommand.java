package everyos.discord.exobot.commands;

import discord4j.core.object.entity.Message;
import everyos.discord.exobot.util.MessageHelper;

public class EchoCommand implements ICommand {
	@Override public void execute(Message message, String argument) {
		if (argument=="") argument = "Cannot echo empty string";
		MessageHelper.send(message.getChannel(), argument, false);
	}

	@Override public String getHelp() {
		return "<text> Echos selected text";
	}

	@Override public COMMANDS getType() {
		return COMMANDS.Test;
	}

	@Override public String getFullHelp() {
		return "**<text>** The text to echo";
	}
}
