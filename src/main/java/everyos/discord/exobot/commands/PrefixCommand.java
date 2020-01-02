package everyos.discord.exobot.commands;

import discord4j.core.object.entity.Message;
import everyos.discord.exobot.util.ChannelHelper;
import everyos.discord.exobot.util.GuildHelper;
import everyos.discord.exobot.util.MessageHelper;
import everyos.discord.exobot.util.UserHelper;

public class PrefixCommand implements ICommand {
	@Override public void execute(Message message, String argument) {
		if (!UserHelper.getUserData(GuildHelper.getGuildData(message.getGuild()), message.getAuthorAsMember()).isOpted()) {
			ChannelHelper.getChannelData(GuildHelper.getGuildData(message.getGuild()), message.getChannel().block())
				.send("User is not opted to use this command", true);
			
			return;
		}
		
		String guildID = GuildHelper.getGuildID(message.getGuild());
		GuildHelper.getGuildData(message.getGuild()).prefix = argument;
		MessageHelper.send(message.getChannel(), "Set new prefix!", true);
	}

	@Override public String getHelp() {
		return "<prefix> Sets the bot's prefix";
	}

	@Override public COMMANDS getType() {
		return COMMANDS.Configuration;
	}
	
	@Override public String getFullHelp() {
		return "**<prefix>** New bot prefix";
	}
}
