package everyos.discord.exobot.commands;

import java.awt.Color;

import discord4j.core.object.entity.Message;
import everyos.discord.exobot.Statics;
import everyos.discord.exobot.objects.ChannelObject;
import everyos.discord.exobot.objects.GuildObject;
import everyos.discord.exobot.util.ChannelHelper;
import everyos.discord.exobot.util.GuildHelper;

public class HelpCommand implements ICommand {
	@Override public void execute(Message message, String argument) {
		GuildObject guild = GuildHelper.getGuildData(message.getGuild().block());
		ChannelObject channel = ChannelHelper.getChannelData(guild, message.getChannel().block());
		if (argument.trim()=="") {
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
		} else {
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
	}

	@Override public String getHelp() {
		return "[command] Displays help for commands of this bot";
	}
	
	@Override public COMMANDS getType() {
		return COMMANDS.Info;
	}
	
	@Override public String getFullHelp() {
		return "[command] Shows full help for command if specified, else shows all commands grouped by type, as well as the bot prefix";
	}
}
