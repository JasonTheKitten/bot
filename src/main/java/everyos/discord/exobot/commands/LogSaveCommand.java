package everyos.discord.exobot.commands;

import discord4j.core.object.entity.Message;
import everyos.discord.exobot.ChannelObject;
import everyos.discord.exobot.GuildObject;
import everyos.discord.exobot.Statics;
import everyos.discord.exobot.util.ChannelHelper;
import everyos.discord.exobot.util.GuildHelper;

public class LogSaveCommand implements ICommand {
	@Override public void execute(Message message, String argument) {
		GuildObject guild = GuildHelper.getGuildData(message.getGuild().block());
		ChannelObject channel = ChannelHelper.getChannelData(guild, message.getChannel().block());
		channel.send(Statics.serializeSave(), false);
	}

	@Override public String getHelp() {
		return "Undocumented";
	}

	@Override public String getFullHelp() {
		return "Undocumented";
	}

	@Override public COMMANDS getType() {
		return COMMANDS.Test;
	}
}
