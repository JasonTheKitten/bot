package everyos.discord.exobot.commands;

import java.awt.Color;

import discord4j.core.object.entity.Message;
import everyos.discord.exobot.ChannelObject;
import everyos.discord.exobot.GuildObject;
import everyos.discord.exobot.Statics;
import everyos.discord.exobot.util.ChannelHelper;
import everyos.discord.exobot.util.GuildHelper;

public class HelpCommand implements ICommand {
	@Override public void execute(Message message, String argument) {
		GuildObject guild = GuildHelper.getGuildData(message.getGuild().block());
		ChannelObject channel = ChannelHelper.getChannelData(guild, message.getChannel().block());
		channel.send(embed->{
			embed.setTitle("Help");
			for (COMMANDS group:ICommand.COMMANDS.values()) {
				StringBuilder commands = new StringBuilder();
				Statics.commands.forEach((k, v)->{
					if (v.getType()==group) {
						String helpmsg = v.getHelp();
						if (helpmsg==null) helpmsg = "<Unknown help>";
						commands.append("**"+k+"**"+helpmsg+"\n");
					}
				});
				String helpstr = commands.toString();
				if (!helpstr.equals(""))
					embed.addField(group.toString(), helpstr.substring(0, helpstr.length()-1), false);
			}
			embed.setFooter("The current bot prefix is \""+guild.prefix+"\"", null);
			embed.setColor(Color.BLUE);
		});
	}

	@Override public String getHelp() {
		return " Displays the Bot's help page";
	}
	
	@Override public COMMANDS getType() {
		return COMMANDS.Info;
	}
	
	@Override public String getFullHelp() {
		return "Shows all commands grouped by type, as well as the bot prefix";
	}
}
