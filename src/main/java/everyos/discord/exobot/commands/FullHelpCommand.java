package everyos.discord.exobot.commands;

import java.awt.Color;

import discord4j.core.object.entity.Message;
import everyos.discord.exobot.ChannelObject;
import everyos.discord.exobot.GuildObject;
import everyos.discord.exobot.Statics;
import everyos.discord.exobot.util.ChannelHelper;
import everyos.discord.exobot.util.GuildHelper;

public class FullHelpCommand implements ICommand {
	@Override public void execute(Message message, String argument) {
		GuildObject guild = GuildHelper.getGuildData(message.getGuild());
		ChannelObject channel = ChannelHelper.getChannelData(guild, message.getChannel().block());
		
		if (argument.trim()=="") { channel.send("At least one parameter required!", true); return; }
		ICommand command = Statics.commands.get(argument);
		if (command==null) { channel.send("No such command!", true); return; }
		channel.send(embed->{
			COMMANDS TYPE = command.getType();
			String group = "Secret";
			if (TYPE!=null) group = TYPE.toString();
			String basicHelp = command.getHelp();
			if (basicHelp == null) basicHelp = "<Unknown>";
			String advancedHelp = command.getFullHelp();
			if (advancedHelp == null) advancedHelp = "<Unknown>";
			
			embed.setTitle(argument+" - Extended Help");
			embed.setDescription(basicHelp);
			embed.addField("Usage Info", advancedHelp, false);
			embed.setColor(Color.RED);
			embed.setFooter("Command group: "+group, null);
		});
	}

	@Override public String getHelp() {
		return "<command> Returns extended usage for a command";
	}

	@Override public String getFullHelp() {
		return "**<command>** The command to get the extended usage of";
	}

	@Override public COMMANDS getType() {
		return COMMANDS.Info;
	}
}
