package everyos.discord.exobot.commands;

import discord4j.core.object.entity.Message;

public interface ICommand {
	public void execute(Message message, String argument);
	public String getHelp();
	public String getFullHelp();
	public COMMANDS getType();
	
	public enum COMMANDS {Configuration, Test, Fun, Custom, Ungrouped, Info, Moderation, Incomplete}
}
